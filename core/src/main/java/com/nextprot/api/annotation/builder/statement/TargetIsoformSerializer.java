package com.nextprot.api.annotation.builder.statement;

import java.io.IOException;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.statements.TargetIsoformStatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetIsoformSerializer {

	public static String serializeToJsonString(TargetIsoformStatement tistatement) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(tistatement);
		} catch (JsonProcessingException e) {
			throw new NextProtException("Failed to convert" + e.getLocalizedMessage());
		}

	}

	public static TargetIsoformStatement deSerializeFromJsonString(String tistatementAsJson) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(tistatementAsJson, TargetIsoformStatement.class);
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}

	}

}
