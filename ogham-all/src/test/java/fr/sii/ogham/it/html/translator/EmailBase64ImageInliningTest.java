package fr.sii.ogham.it.html.translator;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;

public class EmailBase64ImageInliningTest {
	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Before
	public void setUp() throws IOException {
		Properties additionalProperties = new Properties();
		additionalProperties.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProperties.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProperties)
					.and()
				.build();
	}
	
	@Test
	public void imageFormats() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.content(new TemplateContent("/inliner/images/jsoup/source/differentImageFormats.html", new SimpleBean("foo", 42)))
								.to("aurelien.baudet@gmail.com"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/inliner/images/jsoup/expected/differentImageFormatsBase64.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

}
