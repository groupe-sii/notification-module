package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class OverrideDefaultSenderSample {
  public static void main(String[] args) throws MessagingException {
    // configure properties (could be stored in a properties file or defined
    // in System properties)
    Properties properties = new Properties();
    properties.put("mail.smtp.host", "<your server host>");
    properties.put("mail.smtp.port", "<your server port>");
    properties.put("ogham.email.from.default-value", "foo.bar@test.com");  // <1>
    // Instantiate the messaging service using default behavior and
    // provided properties
    MessagingService service = MessagingBuilder.standard()
        .environment()
          .properties(properties)
          .and()
        .build();
    // send the email using fluent API
    service.send(new Email()                                               // <2>
        .subject("OverrideDefaultSenderSample")
        .body().string("email content")
        .to("ogham-test@yopmail.com"));
    // => the sender address is foo.bar@test.com

    service.send(new Email()
        .subject("subject")
        .body().string("email content")
        .from("override@test.com")                                         // <3>
        .to("ogham-test@yopmail.com"));
    // => the sender address is now override@test.com
  }
}
