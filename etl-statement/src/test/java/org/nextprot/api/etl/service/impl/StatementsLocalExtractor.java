package org.nextprot.api.etl.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.service.StatementDictionary;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public class StatementsLocalExtractor extends StatementExtractorBase {

	@Override
	public Set<Statement> getStatementsForSourceForGeneName(NextProtSource notUsed, String geneName) {

		StatementDictionary sd = new StatementDictionary();
		String msh2Content = sd.getStatements(geneName);
		String removedComments = msh2Content.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", "$1");
		
		return deserialize(new ByteArrayInputStream(removedComments.getBytes(StandardCharsets.UTF_8)));

	}

	@Override
	public Set<Statement> getStatementsForSource(NextProtSource source) {
		throw new NextProtException("Method not supported");
	}
	
	
}