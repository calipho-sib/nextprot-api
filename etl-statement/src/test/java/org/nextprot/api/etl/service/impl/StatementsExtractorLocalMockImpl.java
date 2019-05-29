package org.nextprot.api.etl.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.StatementDictionary;
import org.nextprot.commons.statements.Statement;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class StatementsExtractorLocalMockImpl extends StatementExtractorBase {

	@Override
	public Set<Statement> getStatementsFromJsonFile(NextProtSource notUsed, String release, String jsonFilename) {

		StatementDictionary sd = new StatementDictionary();
		String content = sd.getStatements(jsonFilename);
		String removedComments = content.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", "$1");
		
		return deserialize(new ByteArrayInputStream(removedComments.getBytes(StandardCharsets.UTF_8)));

	}

	@Override
	public Set<Statement> getStatementsForSource(NextProtSource source, String release) {
		throw new NextProtException("Method not supported");
	}
	
	
}