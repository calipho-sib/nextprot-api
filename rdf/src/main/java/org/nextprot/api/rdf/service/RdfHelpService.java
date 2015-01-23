package org.nextprot.api.rdf.service;

import java.util.List;

import org.nextprot.api.rdf.domain.RdfTypeInfo;

public interface RdfHelpService {

	List<RdfTypeInfo> getRdfTypeFullInfoList();

	RdfTypeInfo getRdfTypeFullInfo(String rdfType);

	List<String> getRdfTypeValues(String rdfTypeName);

}
