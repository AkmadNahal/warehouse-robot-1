	package com.sap.gateway.content.ip.jdbc;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;


public class IGWCustomDevException extends ODataApplicationException{

	
	private static final long serialVersionUID = 1L;
	protected static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	private IGWCustomDevException(final String localizedmessage, final Locale locale,
			final HttpStatusCodes status, final String errorCode) {
		super(localizedmessage, locale, status, errorCode);
	}

	private IGWCustomDevException(final String localizedMessage, final Locale locale,
			final HttpStatusCodes status, final String errorCode,
			final Throwable e) {
		super(localizedMessage, locale, status, errorCode, e);
	}

	private IGWCustomDevException(final String localizedMessage, final Locale locale,
			final HttpStatusCodes status, final Throwable e) {
		super(localizedMessage, locale, status, e);
	}

	private IGWCustomDevException(final String localizedMessage, final Locale locale,
			final Throwable e) {
		super(localizedMessage, locale, e);
	}

	private IGWCustomDevException(final String localizedMessage, final Locale locale) {
		super(localizedMessage, locale);
	}

	private IGWCustomDevException(final String localizedMessage, final Locale locale,
			final HttpStatusCodes code) {
		super(localizedMessage, locale, code);
	}
	public static IGWCustomDevException throwException(final String localizedmessage,
			final HttpStatusCodes status, final String errorCode) {
		return new IGWCustomDevException(localizedmessage, DEFAULT_LOCALE, status,
				errorCode);
	}

	public static IGWCustomDevException throwException(final String localizedmessage,
			final HttpStatusCodes status) {
		return new IGWCustomDevException(localizedmessage, DEFAULT_LOCALE, status);
	}

	public static IGWCustomDevException throwException(final String localizedmessage) {
		return new IGWCustomDevException(localizedmessage, DEFAULT_LOCALE);
	}


}
