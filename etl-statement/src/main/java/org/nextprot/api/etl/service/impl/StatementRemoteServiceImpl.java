package org.nextprot.api.etl.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.nextprot.api.etl.service.StatementRemoteService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StatementRemoteServiceImpl implements StatementRemoteService{

	private String serviceUrl = "http://kant.isb-sib.ch:9000";

	public StatementRemoteServiceImpl(){
	}
	

	// BioEditor Raw Statement service for a Gene. Example for msh2: http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public List<Statement> getStatementsForSourceForGeneName(NextProtSource source, String geneName) {

		return deserialize(source.getStatementsUrl() + "/gene/"+ geneName + "/statements");
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE) http://kant.isb-sib.ch:9000/bioeditor/statements
	public List<Statement> getStatementsForSource(NextProtSource source) {

		return deserialize(source.getStatementsUrl() + "/statements");
	}

	private List<Statement> deserialize(String url) {

		ObjectMapper mapper = new ObjectMapper();

		List<Statement> obj = null;
		try {
			obj = mapper.readValue(new URL(url), new TypeReference<List<Statement>>() {
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}


}
