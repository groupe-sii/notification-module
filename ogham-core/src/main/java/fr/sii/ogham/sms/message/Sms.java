package fr.sii.ogham.sms.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.capability.HasContentFluent;
import fr.sii.ogham.core.message.capability.HasRecipients;
import fr.sii.ogham.core.message.capability.HasRecipientsFluent;
import fr.sii.ogham.core.message.capability.HasToFluent;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.core.util.StringUtils;

/**
 * SMS message that contains the following information:
 * <ul>
 * <li>The content of the SMS (see {@link Content} and sub classes for more
 * information)</li>
 * <li>The sender information (name and phone number). Name is optional</li>
 * <li>The list of recipients (name and phone number). Name is optional</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Sms implements Message, HasContentFluent<Sms>, HasRecipients<Recipient>, HasRecipientsFluent<Sms, Recipient>, HasToFluent<Sms> {
	/**
	 * The number of the sender
	 */
	private Sender from;

	/**
	 * The list of recipients of the SMS
	 */
	private List<Recipient> recipients;

	/**
	 * The content of the SMS
	 */
	private Content content;
	
	public Sms() {
		super();
		recipients = new ArrayList<>();
	}


	// ----------------------- Getter/Setters -----------------------//

	/**
	 * Get the sender of the SMS
	 * 
	 * @return SMS sender
	 */
	public Sender getFrom() {
		return from;
	}

	/**
	 * Set the sender of the SMS
	 * 
	 * @param from
	 *            SMS sender to set
	 */
	public void setFrom(Sender from) {
		this.from = from;
	}

	/**
	 * Set the sender phone number of the SMS as string.
	 * 
	 * @param phoneNumber
	 *            SMS sender phone number to set
	 */
	public void setFrom(String phoneNumber) {
		setFrom(new Sender(phoneNumber));
	}

	/**
	 * Get the recipients (to) of the SMS
	 * 
	 * @return SMS recipients
	 */
	public List<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the recipients (to) of the SMS
	 * 
	 * @param to
	 *            SMS recipients
	 */
	public void setRecipients(List<Recipient> to) {
		this.recipients = to;
	}

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	// ----------------------- Fluent API -----------------------//

	/**
	 * Set the content of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 */
	public Sms content(Content content) {
		setContent(content);
		return this;
	}

	/**
	 * Set the content of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 */
	public Sms content(String content) {
		return content(new StringContent(content));
	}

	/**
	 * Set the sender.
	 * 
	 * @param from
	 *            the sender
	 * @return this instance for fluent chaining
	 */
	public Sms from(Sender from) {
		setFrom(from);
		return this;
	}

	/**
	 * Set the list of recipients of the message
	 *
	 * @param recipients
	 *            the list of recipients of the message to set
	 * @return this instance for fluent chaining
	 */
	@Override
	public Sms recipients(List<Recipient> recipients) {
		setRecipients(recipients);
		return this;
	}

	/**
	 * Add a recipient for the message
	 *
	 * @param recipients
	 *            one or several recipient to add
	 * @return this instance for fluent chaining
	 */
	@Override
	public Sms recipient(Recipient... recipients) {
		this.recipients.addAll(Arrays.asList(recipients));
		return this;
	}

	/**
	 * Set the sender using the phone number as string.
	 * 
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent chaining
	 */
	public Sms from(String phoneNumber) {
		return from(null, phoneNumber);
	}

	/**
	 * Set the sender using the phone number.
	 * 
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent chaining
	 */
	public Sms from(PhoneNumber phoneNumber) {
		return from(null, phoneNumber);
	}

	/**
	 * Set the sender using the phone number as string.
	 * 
	 * @param name
	 *            the name of the sender
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent chaining
	 */
	public Sms from(String name, String phoneNumber) {
		return from(name, new PhoneNumber(phoneNumber));
	}

	/**
	 * Set the sender using the phone number.
	 * 
	 * @param name
	 *            the name of the sender
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent chaining
	 */
	public Sms from(String name, PhoneNumber phoneNumber) {
		return from(new Sender(name, phoneNumber));
	}

	/**
	 * Add a recipient specifying the phone number.
	 * 
	 * @param numbers
	 *            one or several recipient numbers
	 * @return this instance for fluent chaining
	 */
	public Sms to(PhoneNumber... numbers) {
		for (PhoneNumber number : numbers) {
			to((String) null, number);
		}
		return this;
	}

	/**
	 * Add a recipient specifying the phone number as string.
	 * 
	 * @param numbers
	 *            one or several recipient numbers
	 * @return this instance for fluent chaining
	 */
	public Sms to(String... numbers) {
		for (String num : numbers) {
			to(new Recipient(num));
		}
		return this;
	}

	/**
	 * Add a recipient specifying the name and the phone number.
	 * 
	 * @param name
	 *            the name of the recipient
	 * @param number
	 *            the number of the recipient
	 * @return this instance for fluent chaining
	 */
	public Sms to(String name, PhoneNumber number) {
		to(new Recipient(name, number));
		return this;
	}

	/**
	 * Add a recipient.
	 * 
	 * @param recipients
	 *            one or several recipients to add
	 * @return this instance for fluent chaining
	 */
	public Sms to(Recipient... recipients) {
		recipient(recipients);
		return this;
	}

	// ----------------------- Utilities -----------------------//

	/**
	 * Converts a list of phone numbers to a list of recipients.
	 * 
	 * @param to
	 *            the list of phone numbers
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(PhoneNumber[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (PhoneNumber t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	/**
	 * Converts a list of string to a list of recipients.
	 * 
	 * @param to
	 *            the list of phone numbers as string
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(String[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (String t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sms message\r\nFrom: ").append(from);
		builder.append("\r\nTo: ").append(StringUtils.join(recipients, ", "));
		builder.append("\r\n----------------------------------\r\n").append(content);
		builder.append("\r\n==================================\r\n");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(from, recipients, content).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("from", "recipients", "content").isEqual();
	}
}
