package fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid.Email;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;

/**
 * Implementation of {@link SendGridContentHandler} that delegates content
 * handling to specialized instances, if one matching the actual content type
 * has been declared.
 */
public final class MapContentHandler implements SendGridContentHandler {

	private static final Logger LOG = LoggerFactory.getLogger(MapContentHandler.class);

	private final Map<Class<? extends Content>, SendGridContentHandler> handlers;

	/**
	 * Constructor.
	 */
	public MapContentHandler() {
		this.handlers = new HashMap<>();
	}

	/**
	 * Registers a handler for a given content type.
	 * 
	 * @param clazz
	 *            the content type
	 * @param handler
	 *            the handler
	 * 
	 * @return the handler formerly associated with the content type, if there
	 *         was one
	 */
	public SendGridContentHandler register(final Class<? extends Content> clazz, final SendGridContentHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("[handler] cannot be null");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("[clazz] cannot be null");
		}

		LOG.debug("Registering content handler {} for content type {}", handler, clazz);
		return handlers.put(clazz, handler);
	}

	@Override
	public void setContent(final Email email, final Content content) throws ContentHandlerException {
		if (email == null) {
			throw new IllegalArgumentException("[email] cannot be null");
		}
		if (content == null) {
			throw new IllegalArgumentException("[content] cannot be null");
		}

		final Class<?> clazz = content.getClass();
		LOG.debug("Getting content handler for type {}", clazz);
		final SendGridContentHandler handler = findHandler(clazz);
		if (handler == null) {
			LOG.warn("No content handler found for requested type {}", clazz);
			throw new ContentHandlerException("No content handler found for content type " + clazz.getSimpleName());
		} else {
			handler.setContent(email, content);
		}
	}

	private SendGridContentHandler findHandler(final Class<?> clazz) {
		for(Entry<Class<? extends Content>, SendGridContentHandler> entry : handlers.entrySet()) {
			if(entry.getKey().isAssignableFrom(clazz)) {
				return entry.getValue();
			}
		}
		return null;
	}

}
