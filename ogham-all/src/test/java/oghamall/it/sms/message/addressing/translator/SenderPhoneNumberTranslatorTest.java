package oghamall.it.sms.message.addressing.translator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.sms.builder.SenderNumberBuilder;
import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class SenderPhoneNumberTranslatorTest {
	private PhoneNumberTranslator translator;
	private PhoneNumber givenPhoneNumber;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void before() {
		translator = new SenderNumberBuilder()
				.format()
					.alphanumericCode(true)
					.internationalNumber(true)
					.shortCode(true)
					.build();
	}

	@Test(expected = NullPointerException.class)
	public void translateNull()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = null;

		// when
		AddressedPhoneNumber result = translator.translate(null);

		// then
		Assert.assertNull(result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.UNKNOWN, result.getTon());
	}

	@Test
	public void translateNoNumber()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber(null);

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertNull(result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assert.assertEquals(TypeOfNumber.UNKNOWN, result.getTon());
	}

	@Test
	public void translatePureAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("number");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateAlphanumeric()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("0123456789b");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalButAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+number");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateSeemsShortCodeButAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("TOTO");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateInternational()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+33618160160");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assert.assertEquals(TypeOfNumber.INTERNATIONAL, result.getTon());
	}

	@Test
	public void translateShortCode()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("6184");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateShortCodeMax()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("61845");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalButShortCode()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+618");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assert.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalBut()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("33618160160");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assert.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assert.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assert.assertEquals(TypeOfNumber.UNKNOWN, result.getTon());
	}
}
