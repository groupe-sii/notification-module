package oghamcore.it.core.builder;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.builder.FluentChainingBuilderWithEnv;
import mock.builder.MockBuilder;

public class CustomSenderBuilderTest {
	ExpectedException thrown = ExpectedException.none();
	
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
	
	@Mock EmailSender configuredSender;
	
	@Test
	public void asDeveloperIRegisterACustomBuilderWithFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		CustomChainingBuilder customBuilder = builder.email().sender(CustomChainingBuilder.class);
		customBuilder.someValue(configuredSender);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("parent", customBuilder.and(), is(builder.email()));
		assertThat("build", customBuilder.build(), is(configuredSender));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void asDeveloperIRegisterACustomBuilderWithoutFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		MockBuilder customBuilder = builder.email().sender(MockBuilder.class);
		customBuilder.someValue(configuredSender);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("build", customBuilder.build(), is(configuredSender));
	}
	
	@Test
	public void asDeveloperICantRegisterANonVisible() {
		thrown.expect(BuildException.class);
		thrown.expectCause(instanceOf(IllegalAccessException.class));
		MessagingBuilder builder = MessagingBuilder.empty();
		builder.email().sender(InvisibleBuilder.class);
	}

	@Test
	public void asDeveloperICantRegisterABuilderWithWrongConstructor() {
		thrown.expect(BuildException.class);
		thrown.expect(hasMessage(containsString("No matching constructor found")));
		MessagingBuilder builder = MessagingBuilder.empty();
		builder.email().sender(InvalidBuilder.class);
	}
	
	public static class CustomChainingBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public CustomChainingBuilder(EmailBuilder parent, EnvironmentBuilder<?> env) {
			super(parent, env);
		}
	}
	
	private static class InvisibleBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public InvisibleBuilder(EmailBuilder parent, EnvironmentBuilder<?> env) {
			super(parent, env);
		}
	}
	
	public static class InvalidBuilder implements Builder<EmailSender> {
		public InvalidBuilder(String foo) {
		}
		@Override
		public EmailSender build() {
			return null;
		}
	}
}
