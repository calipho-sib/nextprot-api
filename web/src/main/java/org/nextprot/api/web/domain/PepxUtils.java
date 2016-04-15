package org.nextprot.api.web.domain;

import java.io.IOException;

import org.nextprot.api.commons.exception.NextProtException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PepxUtils {

	/**
	 * Read JSON answer from PepX and get a corresponding Java object
	 * @param content
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static PepXResponse parsePepxResponse(String jsonIsString){

	    try {
			ObjectMapper mapper = new ObjectMapper();
	    	return mapper.readValue(jsonIsString, PepXResponse.class);
	    } catch (IOException e) {
			throw new NextProtException("Some error while reading PepX response " + e.getLocalizedMessage());
		}
	}

}
