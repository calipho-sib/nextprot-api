package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.rdf.RdfTypeInfo;

public interface RdfHelpService {

	List<RdfTypeInfo> getRdfTypeFullInfoList();

	RdfTypeInfo getRdfTypeFullInfo(String rdfType);

	List<String> getRdfTypeValues(String rdfTypeName);

}
