package fr.sii.ogham.testing.assertion.hamcrest;

/**
 * Interface for matchers that are able to provide a message to provide a
 * detailed comparison message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ComparisonAwareMatcher {
	/**
	 * Generate the comparison message
	 * 
	 * @return the detailed message
	 */
	String comparisonMessage();
}
