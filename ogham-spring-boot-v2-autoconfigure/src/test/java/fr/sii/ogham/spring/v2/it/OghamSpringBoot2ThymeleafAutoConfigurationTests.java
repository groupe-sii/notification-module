package fr.sii.ogham.spring.v2.it;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.assertion.OghamAssertions;
import fr.sii.ogham.assertion.OghamInternalAssertions;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;

public class OghamSpringBoot2ThymeleafAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private ApplicationContextRunner contextRunner;
	
	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
						"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"ogham.email.sendgrid.api-key=ogham",
						"spring.sendgrid.api-key=spring",
						"ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name(),
						"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}
	
	@Test
	public void oghamWithThymeleafAutoConfigShouldUseSpringTemplateEngine() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(ThymeleafAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.thymeleaf()
					.all()
						.engine(isA(SpringTemplateEngine.class));
		});
	}
	

	@Test
	public void useCustomThymeleafBean() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(CustomThymeleafEngineConfig.class, ThymeleafAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.thymeleaf()
					.all()
						.engine(isA(CustomSpringTemplateEngine.class));
		});
	}
	

	@Configuration
	protected static class CustomThymeleafEngineConfig {
		
		@Bean
		public SpringTemplateEngine templateEngine() {
			return new CustomSpringTemplateEngine();
		}
	}

	private static class CustomSpringTemplateEngine extends SpringTemplateEngine {
		
	}


	private void checkSms(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content("hello"));
		OghamAssertions.assertThat(smppServer).receivedMessages().count(is(1));
	}

	private void checkEmail(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.subject("test")
				.content("hello"));
		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}
}
