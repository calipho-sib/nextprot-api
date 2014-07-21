package org.nextprot.api.exceptions;

import org.nextprot.search.domain.exception.NextProtException;


public class ConcurrentRequestsException extends NextProtException {
	private static final long serialVersionUID = 1L;

	public ConcurrentRequestsException(String msg) {
		super(msg);
	}
}
