package org.nextprot.api.commons.exception;

public class NextProtException extends RuntimeException {

	private static final long serialVersionUID = 20160913L;

	public NextProtException(Exception e) {
		super(e);
	}

	public NextProtException(String string) {
		super(string);
	}

	public NextProtException(String message, Exception e) {
		super(message, e);
	}
}