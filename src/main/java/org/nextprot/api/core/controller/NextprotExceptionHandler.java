package org.nextprot.api.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.ConcurrentRequestsException;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.controller.error.RestErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Advice class to deal with exception.
 * No stack trace should be returned to the users, always wraps an unexpected exception.
 * 
 * @author dteixeira
 */
@ControllerAdvice
public class NextprotExceptionHandler {

	private static final Log LOGGER = LogFactory.getLog(NextprotExceptionHandler.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NextProtException.class)
	@ResponseBody
	public RestErrorResponse handleNextprotException(NextProtException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ConcurrentRequestsException.class)
	@ResponseBody
	public RestErrorResponse handleTooManyConcurrentRequests(ConcurrentRequestsException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RestErrorResponse handleException(Exception ex) {
		String code = Integer.toHexString(ex.getLocalizedMessage().hashCode() + ex.getClass().getCanonicalName().hashCode()).toUpperCase();
		LOGGER.error("unexpected error occured:" + code + "\t" + ex.getLocalizedMessage());
		ex.printStackTrace();
		return getResponseError("Ups something went wrong.... Try again in a few minutes, if the error persists provide the following code to support : " + code);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntryNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handleException(EntryNotFoundException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseBody
	public RestErrorResponse handleAccessDataException(AccessDeniedException ex) {
		ex.printStackTrace();
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	public RestErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		ex.printStackTrace();
		return getResponseError("The object already exists, choose a different name.");
	}

	public RestErrorResponse getResponseError(String message) {
		RestErrorResponse rer = new RestErrorResponse();
		rer.setMessage(message);
		return rer;

	}

}