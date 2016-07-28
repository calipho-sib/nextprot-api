package com.nextprot.api.annotation.builder.statement;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetIsoformSerializer {

	public static Set<TargetIsoformStatementPosition> deSerializeFromJsonString(String tistatementAsJson) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return new HashSet<>(mapper.readValue(tistatementAsJson, new TypeReference<List<TargetIsoformStatementPosition>>() {
			}));
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}

	}

	public static String serializeToJsonString(Set<TargetIsoformStatementPosition> targetIsoforms) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(targetIsoforms);
		} catch (JsonProcessingException e) {
			throw new NextProtException("Failed to convert" + e.getLocalizedMessage());
		}

	}

}
