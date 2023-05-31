package fr.sii.ogham.testing.extension.spring;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.util.ServerSetup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link GreenMailExtension} bean for JUnit 5</li>
 * <li>Configure port defined by {@code greenmail.smtp.port} property.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class GreenMailTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(GreenMailExtension.class)
	@ConditionalOnProperty("greenmail.smtp.port")
	public GreenMailExtension randomSmtpPortGreenMailExtension(@Value("${greenmail.smtp.port}") int port) {
		return new GreenMailExtension(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
	}
}
