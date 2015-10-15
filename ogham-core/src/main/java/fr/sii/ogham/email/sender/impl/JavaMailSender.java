package fr.sii.ogham.email.sender.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailAttachmentResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailInterceptor;

/**
 * Java mail API implementation.
 * 
 * @author Aurélien Baudet
 * @see JavaMailContentHandler
 */
public class JavaMailSender extends AbstractSpecializedSender<Email> {
	private static final Logger LOG = LoggerFactory.getLogger(JavaMailSender.class);

	/**
	 * Properties that is used to initialize the session
	 */
	private Properties properties;
	
	/**
	 * The content handler used to add message content
	 */
	private JavaMailContentHandler contentHandler;
	
	/**
	 * The attachment handler used to add attachments to the mail
	 */
	private JavaMailAttachmentResourceHandler attachmentHandler;
	
	/**
	 * Extra operations to apply on the message
	 */
	private JavaMailInterceptor interceptor;

	/**
	 * Authentication mechanism
	 */
	private Authenticator authenticator;
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentResourceHandler attachmentResourceHandler, Authenticator authenticator) {
		this(properties, contentHandler, attachmentResourceHandler, authenticator, null);
	}
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentResourceHandler attachmentHandler, Authenticator authenticator, JavaMailInterceptor interceptor) {
		super();
		this.properties = properties;
		this.contentHandler = contentHandler;
		this.attachmentHandler = attachmentHandler;
		this.authenticator = authenticator;
		this.interceptor = interceptor;
	}

	@Override
	public void send(Email email) throws MessageException {
		try {
			LOG.debug("Initialize Java mail session with authenticator {} and properties {}", authenticator, properties);
			// prepare the message
			Session session = Session.getDefaultInstance(properties, authenticator);
			LOG.debug("Create the mime message for email {}", email);
			MimeMessage mimeMsg = new MimeMessage(session);
			// set the sender address
			if(email.getFrom()==null) {
				throw new IllegalArgumentException("The sender address has not been set");
			}
			mimeMsg.setFrom(toInternetAddress(email.getFrom()));
			// set recipients (to, cc, bcc)
			for(Recipient recipient : email.getRecipients()) {
				mimeMsg.addRecipient(convert(recipient.getType()), toInternetAddress(recipient.getAddress()));
			}
			// set subject and content
			mimeMsg.setSubject(email.getSubject());
			LOG.debug("Add message content for email {}", email);
			// create the root as mixed
			MimeMultipart rootContainer = new MimeMultipart("mixed");
			// create the container in case of attachments
			MimeMultipart relatedContainer = new MimeMultipart("related");
			MimeBodyPart relatedPart = new MimeBodyPart();
			relatedPart.setContent(relatedContainer);
			// delegate content management to specialized classes
			contentHandler.setContent(mimeMsg, relatedContainer, email, email.getContent());
			// add attachments to the root or the related container according to the disposition (inline or attached)
			for(Attachment attachment : email.getAttachments()) {
				Multipart container = ContentDisposition.ATTACHMENT.equals(attachment.getDisposition()) ? rootContainer : relatedContainer;
				addAttachment(container, attachment);
			}
			// if no attachments (only text) then root is changed to point on related container
			if(email.getAttachments().isEmpty()) {
				// if no attachments and several parts => set part type to alternative instead of related
				// if no attachments and one part => set part type to mixed instead of related
				rootContainer = relatedContainer;
				if(relatedContainer.getCount()==1) {
					rootContainer.setSubType("mixed");
				} else {
					rootContainer.setSubType("alternative");
				}
			} else {
				// there are attachments so add the related part to the root
				rootContainer.addBodyPart(relatedPart);
			}
			mimeMsg.setContent(rootContainer);
			// default behavior is done => message is ready but let possibility to add extra operations to do on the message
			if(interceptor!=null) {
				LOG.debug("Executing extra operations for email {}", email);
				interceptor.intercept(mimeMsg, email);
			}
			// message is ready => send it
			LOG.info("Sending email using Java Mail API through server {}:{}...", properties.getProperty("mail.smtp.host", properties.getProperty("mail.host")), properties.getProperty("mail.smtp.port", properties.getProperty("mail.port")));
			Transport.send(mimeMsg);
		} catch (UnsupportedEncodingException | MessagingException | ContentHandlerException | AttachmentResourceHandlerException e) {
			throw new MessageException("failed to send message using Java Mail API", email, e);
		}
	}
	
	private void addAttachment(Multipart multipart, Attachment attachment) throws AttachmentResourceHandlerException {
		MimeBodyPart part = new MimeBodyPart();
		try {
			part.setFileName(attachment.getResource().getName());
			part.setDisposition(attachment.getDisposition());
			part.setDescription(attachment.getDescription());
			part.setContentID(attachment.getContentId());
			attachmentHandler.setData(part, attachment.getResource(), attachment);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new AttachmentResourceHandlerException("Failed to attach "+attachment.getResource().getName(), attachment, e);
		}
	}
	
	private RecipientType convert(fr.sii.ogham.email.message.RecipientType type) {
		switch(type) {
			case BCC:
				return RecipientType.BCC;
			case CC:
				return RecipientType.CC;
			case TO:
				return RecipientType.TO;
		}
		throw new IllegalArgumentException("Invalid recipient type "+type);
	}
	
	private static InternetAddress toInternetAddress(EmailAddress address) throws AddressException, UnsupportedEncodingException {
		return address.getPersonal()==null ? new InternetAddress(address.getAddress()) : new InternetAddress(address.getAddress(), address.getPersonal());
	}

	@Override
	public String toString() {
		return "JavaMailSender";
	}
}
