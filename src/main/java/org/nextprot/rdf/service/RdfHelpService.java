package org.nextprot.rdf.service;

import java.util.List;

import org.nextprot.rdf.domain.RdfTypeInfo;

public interface RdfHelpService {

	List<RdfTypeInfo> getRdfTypeFullInfoList();

	RdfTypeInfo getRdfTypeFullInfo(String rdfType);

	List<String> getRdfTypeValues(String rdfTypeName);

}
