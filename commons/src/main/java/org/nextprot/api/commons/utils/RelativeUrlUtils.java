package org.nextprot.api.commons.utils;

public class RelativeUrlUtils {

	static public String[] getPathElements(String url) {
		if (url.startsWith("/")) url = url.substring(1);
		int paramsIdx=url.indexOf("?");
		if (paramsIdx != -1) url = url.substring(0,paramsIdx);
		return url.split("/");
	}

	static public String[] getParamsElements(String url) {
		if (url.startsWith("/")) url = url.substring(1);
		int paramsIdx=url.indexOf("?");
		if (paramsIdx == -1) return new String[0]; 
		url = url.substring(paramsIdx+1);
		return url.split("&");
	}

	
}
