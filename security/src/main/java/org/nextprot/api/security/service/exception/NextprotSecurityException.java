package org.nextprot.api.security.service.exception;

import org.nextprot.api.commons.exception.NextProtException;

public class NextprotSecurityException extends NextProtException {
	private static final long serialVersionUID = 1L;

	public NextprotSecurityException(String msg) {
		super(msg);
	}

	public NextprotSecurityException(Exception e) {
		super(e);
	}
}
