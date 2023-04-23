package fr.sii.ogham.spring.util.compat;

import jakarta.servlet.http.HttpServletResponse;

public class JakartaHttpServletResponseWrapper implements HttpServletResponseWrapper {
	private final HttpServletResponse response;
	
	public JakartaHttpServletResponseWrapper(HttpServletResponse response) {
		super();
		this.response = response;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) response;
	}

}
