package org.nextprot.api.etl.statement.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.nextprot.api.etl.statement.service.RawStatementRemoteService;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class RawStatementRemoteServiceImpl implements RawStatementRemoteService{

	private String serviceUrl = "http://kant.isb-sib.ch:9000";

	public RawStatementRemoteServiceImpl(){
	}
	

	// BioEditor Raw Statement service for a Gene. Example for msh2: http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public List<RawStatement> getStatementsForSourceForGeneName(String source, String geneName) {

		return deserialize(serviceUrl + "/" + source + "/gene/"+ geneName + "/statements");
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE) http://kant.isb-sib.ch:9000/bioeditor/statements
	public List<RawStatement> getStatementsForSource(String source) {

		return deserialize(serviceUrl + "/" + source + "/statements");
	}

	private List<RawStatement> deserialize(String url) {

		ObjectMapper mapper = new ObjectMapper();

		List<RawStatement> obj = null;
		try {
			obj = mapper.readValue(new URL(url), new TypeReference<List<RawStatement>>() {
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}


}
