package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.MessageSpecificFreemarkerProperties;
import fr.sii.ogham.spring.common.MessageSpecificTemplateProperties;
import fr.sii.ogham.spring.common.MessageSpecificThymeleafProperties;
import fr.sii.ogham.spring.common.OghamTemplateProperties;

@ConfigurationProperties("ogham.sms")
public class OghamSmsProperties implements OghamTemplateProperties {
	/**
	 * If no sender phone number is explicitly defined on the message, Ogham
	 * will use this phone number as default sender number.
	 */
	private String from;
	/**
	 * If no recipient phone number is explicitly defined on the message, Ogham
	 * will use this phone number as default recipient number.
	 */
	private String to;
	/**
	 * Enable/disable alphanumeric code format conversion for sender phone
	 * number: if the sender address is alphanumeric (contains both letters and
	 * numbers) or non-numeric, TON is set to 5 and NPI to 0.
	 */
	private boolean fromFormatEnableAlphanumeric = true;
	/**
	 * Enable/disable short code format conversion for sender phone number: if
	 * the sender address is a short code, TON is set to 3, and NPI is set to 0.
	 * A number is considered to be a short code if the length of the number is
	 * 5 digits or less.
	 */
	private boolean fromFormatEnableShortcode = true;
	/**
	 * Enable/disable international number format conversion for sender phone
	 * number: if the sender starts with a "+", TON is set to 1, and NPI is set
	 * to 1.
	 * 
	 */
	private boolean fromFormatEnableInternational = true;
	/**
	 * Enable/disable international number format conversion for recipient phone
	 * number: if the sender starts with a "+", TON is set to 1, and NPI is set
	 * to 1.
	 * 
	 */
	private boolean toFormatEnableInternational = true;
	@NestedConfigurationProperty
	private MessageSpecificFreemarkerProperties freemarker = new MessageSpecificFreemarkerProperties();
	@NestedConfigurationProperty
	private MessageSpecificThymeleafProperties thymeleaf = new MessageSpecificThymeleafProperties();
	@NestedConfigurationProperty
	private MessageSpecificTemplateProperties template = new MessageSpecificTemplateProperties();

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean isFromFormatEnableAlphanumeric() {
		return fromFormatEnableAlphanumeric;
	}

	public void setFromFormatEnableAlphanumeric(boolean fromFormatEnableAlphanumeric) {
		this.fromFormatEnableAlphanumeric = fromFormatEnableAlphanumeric;
	}

	public boolean isFromFormatEnableShortcode() {
		return fromFormatEnableShortcode;
	}

	public void setFromFormatEnableShortcode(boolean fromFormatEnableShortcode) {
		this.fromFormatEnableShortcode = fromFormatEnableShortcode;
	}

	public boolean isFromFormatEnableInternational() {
		return fromFormatEnableInternational;
	}

	public void setFromFormatEnableInternational(boolean fromFormatEnableInternational) {
		this.fromFormatEnableInternational = fromFormatEnableInternational;
	}

	public boolean isToFormatEnableInternational() {
		return toFormatEnableInternational;
	}

	public void setToFormatEnableInternational(boolean toFormatEnableInternational) {
		this.toFormatEnableInternational = toFormatEnableInternational;
	}

	public MessageSpecificFreemarkerProperties getFreemarker() {
		return freemarker;
	}

	public void setFreemarker(MessageSpecificFreemarkerProperties freemarker) {
		this.freemarker = freemarker;
	}

	public MessageSpecificThymeleafProperties getThymeleaf() {
		return thymeleaf;
	}

	public void setThymeleaf(MessageSpecificThymeleafProperties thymeleaf) {
		this.thymeleaf = thymeleaf;
	}

	public MessageSpecificTemplateProperties getTemplate() {
		return template;
	}

	public void setTemplate(MessageSpecificTemplateProperties template) {
		this.template = template;
	}
}
