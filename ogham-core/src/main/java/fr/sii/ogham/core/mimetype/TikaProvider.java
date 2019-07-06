package fr.sii.ogham.core.mimetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.charset.FixedCharsetDetector;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Mime Type detection based on <a href="http://tika.apache.org/">Apache
 * Tika</a>. This library is very complete and up-to-date. It is also able to
 * parse documents but this is not useful in our case.
 * 
 * @author Aurélien Baudet
 * @see <a href="http://tika.apache.org/">Apache Tika</a>
 *
 */
public class TikaProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(TikaProvider.class);

	/**
	 * The Tika instance to use
	 */
	private final Tika tika;

	/**
	 * Whether to fail if the default mimetype is return (this may indicate that
	 * detection hasn't work).
	 */
	private final boolean failIfOctetStream;

	/**
	 * Used to automatically detect charset
	 */
	private final CharsetDetector charsetDetector;

	/**
	 * Initialize the provider with default Tika instance and configuration. It
	 * fails if application/octet-stream mimetype is returned
	 */
	public TikaProvider() {
		this(new Tika(), true);
	}

	/**
	 * Initialize the provider with the specified Tika instance. No charset
	 * detection is used, always returning UTF-8.
	 * 
	 * @param tika
	 *            the Tika instance to use
	 * @param failIfOctetStream
	 *            Whether to fail if the default mimetype is return (this may
	 *            indicate that detection hasn't work).
	 */
	public TikaProvider(Tika tika, boolean failIfOctetStream) {
		this(tika, failIfOctetStream, new FixedCharsetDetector());
	}

	/**
	 * Initialize the provider with the specified Tika instance.
	 * 
	 * @param tika
	 *            the Tika instance to use
	 * @param failIfOctetStream
	 *            Whether to fail if the default mimetype is return (this may
	 *            indicate that detection hasn't work).
	 * @param charsetDetector
	 *            used to detect content encoding
	 */
	public TikaProvider(Tika tika, boolean failIfOctetStream, CharsetDetector charsetDetector) {
		super();
		this.tika = tika;
		this.failIfOctetStream = failIfOctetStream;
		this.charsetDetector = charsetDetector;
	}

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type for file {}", file);
			String mimetype = tika.detect(file);
			LOG.debug("Detect mime type for file {}: {}", file, mimetype);
			checkMimeType(mimetype);
			return new MimeType(mimetype);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		} catch (IOException e) {
			throw new MimeTypeDetectionException("Failed to get the mimetype for the file " + file, e);
		}
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		return getMimeType(new File(fileName));
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type from stream");
			String mimetype = tika.detect(stream);
			LOG.debug("Detect mime type from stream: {}", mimetype);
			checkMimeType(mimetype);
			return new MimeType(mimetype);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		} catch (IOException e) {
			throw new MimeTypeDetectionException("Failed to get the mimetype because the stream is not readable", e);
		}
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type from stream");
			String mimetype = tika.detect(content.getBytes(charsetDetector.detect(content)));
			LOG.debug("Detect mime type from stream: {}", mimetype);
			checkMimeType(mimetype);
			return new MimeType(mimetype);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		}
	}

	@Override
	public MimeType detect(String content, Charset charset) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type from stream");
			String mimetype = tika.detect(content.getBytes(charset));
			LOG.debug("Detect mime type from stream: {}", mimetype);
			checkMimeType(mimetype);
			return new MimeType(mimetype);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TikaProvider [tika=").append(tika.toString()).append("]");
		return builder.toString();
	}

	private void checkMimeType(String mimetype) throws MimeTypeDetectionException {
		if (failIfOctetStream && MediaType.OCTET_STREAM.toString().equals(mimetype)) {
			throw new MimeTypeDetectionException("Default mimetype found (application/octet-stream) but provider is configured to fail in this case");
		}
	}

}
