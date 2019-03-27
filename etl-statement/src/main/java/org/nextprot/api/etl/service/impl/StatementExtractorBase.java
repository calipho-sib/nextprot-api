package org.nextprot.api.etl.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

abstract class StatementExtractorBase implements StatementExtractorService {

	protected Collection<Statement> deserialize(InputStream content) {

		ObjectMapper mapper = new ObjectMapper();

		Set<Statement> obj = null;
		try {
			obj = mapper.readValue(content, new TypeReference<Set<Statement>>() { });
		} catch (IOException e) {
			throw new NextProtException(e);
		}

		return obj;
	}


}
