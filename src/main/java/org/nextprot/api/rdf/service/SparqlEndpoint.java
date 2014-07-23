package org.nextprot.api.rdf.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.rdf.domain.TripleInfo;

import com.hp.hpl.jena.query.QueryExecution;


public interface SparqlEndpoint {

	public String getTimeout();

	public String getUrl();

	public QueryExecution queryExecution(String query);
	
	public List<TripleInfo> getTripleInfoList(String rdfType);

	public Set<String> getRdfTypeValues(String rdfTypeName, int limitResult);
	
	public Map<String, String> getRdfTypeProperties(String rdfType);

	public Set<String> getAllRdfTypesNames();

	public 	Set<String> getValuesForTriple(String rdfTypeName, String objectType, int limitResult);


}
