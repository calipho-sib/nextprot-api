package org.nextprot.api.core.aop.requests;

import java.util.TreeMap;

public class RequestInfoFactory extends TreeMap<String, String> {

	private static final long serialVersionUID = 5171958531007317652L;

	public static RequestInfo createRequestInfo(String identifier) {
		return new RequestInfo(identifier);
	}

}