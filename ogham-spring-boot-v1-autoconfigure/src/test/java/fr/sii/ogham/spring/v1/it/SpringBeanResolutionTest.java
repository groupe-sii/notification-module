package fr.sii.ogham.spring.v1.it;

import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static fr.sii.ogham.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.assertion.OghamAssertions;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import freemarker.core.InvalidReferenceException;

public class SpringBeanResolutionTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	@Rule public final ExpectedException thrown = ExpectedException.none();

	private AnnotationConfigApplicationContext context;
	private MessagingService messagingService;
	
	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, 
				"mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
				"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
				"ogham.sms.smpp.host=127.0.0.1",
				"ogham.sms.smpp.port="+smppServer.getPort(),
				"spring.freemarker.suffix=");
		context.register(TestConfig.class, ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		messagingService = context.getBean(MessagingService.class);
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void emailUsingThymeleafTemplateShouldResolveBeans() throws MessagingException, IOException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/thymeleaf/source/bean-resolution.html", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isSimilarHtml(resourceAsString("/thymeleaf/expected/bean-resolution.html")));
	}

	@Test
	public void smsUsingThymeleafTemplateShouldResolveBeans() throws MessagingException, IOException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/thymeleaf/source/bean-resolution.txt", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(smppServer)
		.receivedMessages()
			.count(is(1))
			.message(0)
				.content(is(resourceAsString("/thymeleaf/expected/bean-resolution.txt")));
	}

	@Test
	public void missingBeanErrorUsingThymeleaf() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(NoSuchBeanDefinitionException.class, hasMessage("No bean named 'missingService' available"))));
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/thymeleaf/source/missing-bean.html", new SimpleBean("world", 0))));
	}
	
	@Test
	public void emailUsingFreemarkerTemplateShouldResolveBeans() throws MessagingException, IOException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/freemarker/source/bean-resolution.html.ftl", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isSimilarHtml(resourceAsString("/freemarker/expected/bean-resolution.html")));
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldResolveBeans() throws MessagingException, IOException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/freemarker/source/bean-resolution.txt.ftl", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(smppServer)
		.receivedMessages()
			.count(is(1))
			.message(0)
				.content(is(resourceAsString("/freemarker/expected/bean-resolution.txt")));
	}

	@Test
	public void missingBeanErrorUsingFreemarker() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> @missingService")))));
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/freemarker/source/missing-bean.html.ftl", new SimpleBean("world", 0))));
	}


	

	@Configuration
	protected static class TestConfig {
		
		@Bean
		public FakeService fakeService() {
			return new FakeService();
		}
	}
	
	public static class FakeService {
		public String hello(String name) {
			return "hello " + name;
		}
	}
}