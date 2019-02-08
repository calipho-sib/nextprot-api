package org.nextprot.api.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.ConcurrentRequestsException;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.exception.EntrySetNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.exception.ResourceNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Advice class to deal with exception. No stack trace should be returned to the
 * users, always wraps an unexpected exception.
 * 
 * @author dteixeira
 */
@ControllerAdvice
public class NextprotExceptionHandler {

	private static final Log LOGGER = LogFactory.getLog(NextprotExceptionHandler.class);

	private static final String ENTRIES_NOT_FOUND = "entriesNotFound";

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(NotAuthorizedException.class)
	@ResponseBody
	public RestErrorResponse handle(NotAuthorizedException ex) {
		LOGGER.info("Not authorized" + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NextProtException.class)
	@ResponseBody
	public RestErrorResponse handle(NextProtException ex) {
		LOGGER.info("NextProt exception" + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ConcurrentRequestsException.class)
	@ResponseBody
	public RestErrorResponse handle(ConcurrentRequestsException ex) {
		LOGGER.error("Too many requests!!!!");
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(JsonProcessingException.class)
	@ResponseBody
	public RestErrorResponse handle(JsonProcessingException ex) {
		LOGGER.warn("Json Processing " + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMediaTypeException.class)
	@ResponseBody
	public RestErrorResponse handle(HttpMediaTypeException ex) {
		LOGGER.warn("Bad request " + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handle(ResourceNotFoundException ex) {
		LOGGER.warn("Resource not found " + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(DataAccessException.class)
	@ResponseBody
	public RestErrorResponse handle(DataAccessException ex) {
		LOGGER.warn("Data access exception " + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public RestErrorResponse handle(MissingServletRequestParameterException ex) {
		LOGGER.error("Missing parameter: " + ex.getLocalizedMessage());
		ex.printStackTrace();
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RestErrorResponse handle(Exception ex) {
		LOGGER.error("unexpected error occurred:" + ex.getLocalizedMessage());
		ex.printStackTrace();
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntryNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handle(EntryNotFoundException ex) {
		LOGGER.warn("Entry not found exception " + ex.getLocalizedMessage());
		RestErrorResponse response = getResponseError(ex);

        Set<String> set = new HashSet<>();
        set.add(ex.getEntry());

        response.setProperty(ENTRIES_NOT_FOUND, (HashSet)set);

		return response;
	}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntrySetNotFoundException.class)
    @ResponseBody
    public RestErrorResponse handle(EntrySetNotFoundException ex) {
        LOGGER.warn("Entry set not found exception " + ex.getLocalizedMessage());
        RestErrorResponse response = getResponseError(ex);

        response.setProperty(ENTRIES_NOT_FOUND, (HashSet)ex.getEntrySet());

        return response;
    }

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseBody
	public RestErrorResponse handle(AccessDeniedException ex) {
		LOGGER.warn("An error occurred: " + ex.getLocalizedMessage());
		return getResponseError(ex);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	public RestErrorResponse handle(DataIntegrityViolationException ex) {
		LOGGER.warn("Data Integration violation occurred " + ex.getLocalizedMessage());
		ex.printStackTrace();
		return getResponseErrorMsg("conflict with another resource (try to use a different name)");
	}

	private static RestErrorResponse getResponseError(Throwable t) {
		t.printStackTrace();
		RestErrorResponse errorResponse = getResponseErrorMsg(t.getLocalizedMessage());
		errorResponse.setType(t.getClass().getSimpleName());
		return errorResponse;
	}

	private static RestErrorResponse getResponseErrorMsg(String message) {
		RestErrorResponse rer = new RestErrorResponse();
		rer.setMessage(message);
		return rer;
	}
}