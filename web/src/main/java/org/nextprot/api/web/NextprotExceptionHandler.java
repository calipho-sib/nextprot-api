package org.nextprot.api.web;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.nextprot.api.commons.exception.ConcurrentRequestsException;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
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

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(NotAuthorizedException.class)
	@ResponseBody
	public RestErrorResponse handle(NotAuthorizedException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NextProtException.class)
	@ResponseBody
	public RestErrorResponse handle(NextProtException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ConcurrentRequestsException.class)
	@ResponseBody
	public RestErrorResponse handle(ConcurrentRequestsException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(JsonProcessingException.class)
	@ResponseBody
	public RestErrorResponse handle(JsonProcessingException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMediaTypeException.class)
	@ResponseBody
	public RestErrorResponse handle(HttpMediaTypeException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handle(ResourceNotFoundException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(DataAccessException.class)
	@ResponseBody
	public RestErrorResponse handle(DataAccessException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RestErrorResponse handle(Exception ex) {
		String code = Integer.toHexString(ex.getLocalizedMessage().hashCode() + ex.getClass().getCanonicalName().hashCode()).toUpperCase();
		LOGGER.error("unexpected error occurred:" + code + "\t" + ex.getLocalizedMessage());
		ex.printStackTrace();
		return getResponseError("Oops something went wrong.... Try again in a few minutes, if the error persists provide the following code to support : " + code);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntryNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handle(EntryNotFoundException ex) {
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseBody
	public RestErrorResponse handle(AccessDeniedException ex) {
		ex.printStackTrace();
		return getResponseError(ex.getLocalizedMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	public RestErrorResponse handle(DataIntegrityViolationException ex) {
		ex.printStackTrace();
		return getResponseError("conflict with another resource (try to use a different name)");
	}

	public RestErrorResponse getResponseError(String message) {
		RestErrorResponse rer = new RestErrorResponse();
		rer.setMessage(message);
		return rer;

	}

}