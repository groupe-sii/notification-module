package fr.sii.ogham.spring.v1.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.spring.common.OghamMimetypeProperties;
import fr.sii.ogham.spring.common.SpringEnvironmentConfigurer;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.email.OghamJavaMailConfiguration;
import fr.sii.ogham.spring.sms.OghamCloudhopperConfiguration;
import fr.sii.ogham.spring.sms.OghamOvhSmsConfiguration;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.OghamCommonTemplateProperties;
import fr.sii.ogham.spring.template.OghamFreemarkerConfiguration;
import fr.sii.ogham.spring.template.OghamNoTemplateEngineConfiguration;
import fr.sii.ogham.spring.v1.email.OghamSendGridV2Configuration;
import fr.sii.ogham.spring.v1.template.OghamThymeleafV2Configuration;

/**
 * <p>
 * Spring Boot auto-configuration module for Ogham messaging library.
 * </p>
 * 
 * It links Ogham with Spring beans:
 * <ul>
 * <li>Use SpringTemplateEngine instead of default Thymeleaf TemplateEngine</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 */
// @formatter:off
@Configuration
@AutoConfigureAfter({ WebMvcAutoConfiguration.class,
						ThymeleafAutoConfiguration.class, 
						FreeMarkerAutoConfiguration.class, 
						MailSenderAutoConfiguration.class })
@ConditionalOnClass({ /* used to match Spring Boot 1 */ WebMvcAutoConfiguration.class, MessagingService.class, MessagingBuilder.class })
@ConditionalOnMissingBean(MessagingService.class)
@EnableConfigurationProperties({ OghamEmailProperties.class, 
								 OghamSmsProperties.class, 
								 OghamMimetypeProperties.class,
								 OghamCommonTemplateProperties.class })
@Import({ OghamNoTemplateEngineConfiguration.class, 
							OghamFreemarkerConfiguration.class, 
							OghamThymeleafV2Configuration.class, 
							OghamJavaMailConfiguration.class,
							OghamSendGridV2Configuration.class,
							OghamCloudhopperConfiguration.class,
							OghamOvhSmsConfiguration.class })
//@formatter:on
public class OghamSpringBoot1AutoConfiguration {

	/**
	 * Configures the Messaging service and the {@link TemplateParser}. A
	 * ThymeLeaf parser will be configured. If we find SpringTemplateEngine, we
	 * will set it as its template engine implementation. If we find a
	 * FreeMarker configuration already configured by spring-boot, we will add a
	 * FreeMarker parser.
	 * 
	 * @param builder
	 *            The builder used to create the messaging service
	 * 
	 * @return A configured messaging service
	 */
	@Bean
	public MessagingService messagingService(MessagingBuilder builder) {
		builder.configure();
		return builder.build();
	}

	@Bean
	public MessagingBuilder defaultMessagingBuilder(List<SpringMessagingConfigurer> configurers) {
		MessagingBuilder builder = MessagingBuilder.standard(false);
		for (SpringMessagingConfigurer configurer : configurers) {
			builder.register(configurer, configurer.getOrder());
		}
		return builder;
	}

	@Bean
	public SpringEnvironmentConfigurer springEnvironmentConfigurer(Environment environment) {
		return new SpringEnvironmentConfigurer(environment);
	}
}
