package fr.sii.ogham.ut.subject.provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.subject.provider.TextPrefixSubjectProvider;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.junit.LoggingTestRule;

public class TextPrefixSubjectProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private TextPrefixSubjectProvider subjectProvider;
	
	@Before
	public void setUp() {
		subjectProvider = new TextPrefixSubjectProvider();
	}
	
	@Test
	public void withPrefix() {
		Email message = new Email().content("Subject: this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assert.assertEquals("subject should be 'this is the subject'", "this is the subject", subject);
		Assert.assertEquals("Content should be updated", "Content of the email", message.getContent().toString());
	}
	
	@Test
	public void trim() {
		Email message = new Email().content("Subject:    this is the subject    \nContent of the email");
		String subject = subjectProvider.provide(message);
		Assert.assertEquals("subject should be 'this is the subject'", "this is the subject", subject);
		Assert.assertEquals("Content should be updated", "Content of the email", message.getContent().toString());
	}
	
	@Test
	public void emptySubject() {
		Email message = new Email().content("Subject:\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assert.assertTrue("subject should be empty", subject.isEmpty());
		Assert.assertEquals("Content should be updated", "Content of the email", message.getContent().toString());
	}
	
	@Test
	public void malformedPrefix() {
		Email message = new Email().content("subject: this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assert.assertNull("subject should be null", subject);
		Assert.assertEquals("Content should not be updated", "subject: this is the subject\nContent of the email", message.getContent().toString());
	}
	
	@Test
	public void noPrefix() {
		Email message = new Email().content("this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assert.assertNull("subject should be null", subject);
		Assert.assertEquals("Content should not be updated", "this is the subject\nContent of the email", message.getContent().toString());
	}
}
