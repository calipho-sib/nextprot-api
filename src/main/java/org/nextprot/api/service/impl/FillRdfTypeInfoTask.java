package org.nextprot.api.service.impl;

import java.util.concurrent.Callable;

import org.nextprot.api.domain.rdf.RdfTypeInfo;
import org.nextprot.api.service.RdfHelpService;

public class FillRdfTypeInfoTask implements Callable<RdfTypeInfo> {

	private String rdfType = null;

	private RdfHelpService rdfTypeInfoService;

	public FillRdfTypeInfoTask(RdfHelpService rdfTypeInfoService, String rdfType) {
		this.rdfType = rdfType;
		this.rdfTypeInfoService = rdfTypeInfoService;
	}

	@Override
	public RdfTypeInfo call() {
		System.out.println("Calling " + rdfType);
		return rdfTypeInfoService.getRdfTypeFullInfo(rdfType);
	}


}
