package oghamcore.ut.email.subject.provider;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class HtmlTitleSubjectProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private HtmlTitleSubjectProvider subjectProvider;
	
	@Before
	public void setUp() {
		subjectProvider = new HtmlTitleSubjectProvider();
	}

	@Test
	public void withTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/withTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assert.assertEquals("subject should be 'this is the subject'", "this is the subject", subject);
	}
	
	@Test
	public void trim() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/trim.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assert.assertEquals("subject should be 'this is the subject'", "this is the subject", subject);
	}
	
	@Test
	public void emptyTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/emptyTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assert.assertTrue("subject should be empty", subject.isEmpty());
	}
	
	@Test
	public void noTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/noTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assert.assertNull("subject should be null", subject);
	}
}
