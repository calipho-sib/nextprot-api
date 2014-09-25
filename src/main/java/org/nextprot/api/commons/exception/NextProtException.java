package org.nextprot.api.commons.exception;

public class NextProtException extends RuntimeException {

	private static final long serialVersionUID = 7136339068710890756L;

	public NextProtException(Exception e) {
		super(e);
	}

	public NextProtException(String string) {
		super(string);
	}


}