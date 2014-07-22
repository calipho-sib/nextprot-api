package org.nextprot.core.exception;



public class ConcurrentRequestsException extends NextProtException {
	private static final long serialVersionUID = 1L;

	public ConcurrentRequestsException(String msg) {
		super(msg);
	}
}
