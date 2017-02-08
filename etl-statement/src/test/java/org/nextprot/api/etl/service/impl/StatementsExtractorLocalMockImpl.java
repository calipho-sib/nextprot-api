package org.nextprot.api.etl.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.service.StatementDictionary;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public class StatementsExtractorLocalMockImpl extends StatementExtractorBase {

	@Override
	public Set<Statement> getStatementsForSourceForGeneName(NextProtSource notUsed, String release, String geneName) {

		StatementDictionary sd = new StatementDictionary();
		String content = sd.getStatements(geneName);
		String removedComments = content.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", "$1");
		
		return deserialize(new ByteArrayInputStream(removedComments.getBytes(StandardCharsets.UTF_8)));

	}

	@Override
	public Set<Statement> getStatementsForSource(NextProtSource source, String release) {
		throw new NextProtException("Method not supported");
	}
	
	
}