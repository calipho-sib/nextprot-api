package org.nextprot.api.commons.utils;

import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {

	public static ObjectMapper getObjectMapper(){
		
		ObjectMapper mapper = new ObjectMapper();
		// SerializationFeature for changing how JSON is written

		// to enable standard indentation ("pretty-printing"):
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// to allow serialization of "empty" POJOs (no properties to serialize)
		// (without this setting, an exception is thrown in those cases)
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// to write java.util.Date, Calendar as number (timestamp):
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return mapper;
	}
	
	public static String getRepresentationInString(ObjectMapper mapper, ObjectNode root){
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			mapper.writeValue(out, root);
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
