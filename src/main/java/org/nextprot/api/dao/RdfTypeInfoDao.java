package org.nextprot.api.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.domain.rdf.TripleInfo;


public interface RdfTypeInfoDao {

	List<TripleInfo> getTripleInfoList(String rdfType);

	Set<String> getRdfTypeValues(String rdfTypeName, int limitResult);
	
	Map<String, String> getRdfTypeProperties(String rdfType);

	Set<String> getAllRdfTypesNames();

	Set<String> getValuesForTriple(String rdfTypeName, String objectType, int limitResult);

}
