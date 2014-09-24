package org.nextprot.api.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;

public class SparqlUtils {

	public static final SparqlResult convertResultToFormat(ResultSet resultSet, ResultsFormat format) {
		
		SparqlResult result = new SparqlResult();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSetFormatter.output(baos, resultSet, format);
		// resultSet.reset();
		try {
		
			result.setNumRows(resultSet.getRowNumber());
			result.setFormat(format.getSymbol());
			result.setOutput(baos.toString("UTF-8"));

		} catch (UnsupportedEncodingException e) {
			// should never happen as UTF-8 is supported
			throw new Error(e);
		}
		
		return result;
	}

	public static final String convertResultSetToXML(ResultSet resultSet) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsXML(baos, resultSet);
		// resultSet.reset();
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen as UTF-8 is supported
			throw new Error(e);
		}
	}

	/**
	 * read meta info 'ac' in sparql query comment
	 * ac variable define the values we want to get as result
	 * 
	 * @param query
	 * @return
	 */
	public static String getQueryMetaAc(String query) {
		String split[] = query.split("#ac:");
		if (split.length == 0)
			return "";
		return split[1].replaceAll("\\n.*", "");
	}

	/**
	 * read meta info 'count' in sparql query comment
	 * count variable define the size of the result
	 * 
	 * @param query
	 * @return
	 */
	public static int getQueryMetaCount(String query) {
		String split[] = query.split("#count:");
		if (split.length < 2)
			return 0;
		return Integer.parseInt(split[1].replaceAll("\\n.*", ""));
	}

	/**
	 * read meta info 'title' in sparql query comment
	 * title variable define the query title
	 * 
	 * @param query
	 * @return
	 */
	public static String getQueryMetaTitle(String query) {
		Pattern p = Pattern.compile("^#title:(.+)$", Pattern.DOTALL | Pattern.MULTILINE);
		String split[] = query.split("#title:");
		if (split.length == 0)
			return "";
		return split[1].replaceAll("\\n.*", "");
	}

	/**
	 * Get meta info from sparql query
	 * 
	 * @param query
	 * @return
	 */
	public static Map<String, String> getMetaInfo(String query) {
		
		Map<String,String> meta=new HashMap<String, String>();
		//
		// get id and host
		Matcher m=Pattern.compile("#id:([^ ]+).?endpoint:([^\\n]*)",Pattern.DOTALL | Pattern.MULTILINE).matcher(query);
		if(m.find()){
			meta.put("id", m.group(1));
			meta.put("endpoint", m.group(2));
		}
		
		//
		// get acs
		m=Pattern.compile("[# ]?ac:([^ \\n]*)",Pattern.DOTALL | Pattern.MULTILINE).matcher(query);
		if(m.find()){
			meta.put("acs", m.group(1));
		}

		//
		// get count
		m=Pattern.compile("[# ]?count:([^\\n]*)",Pattern.DOTALL | Pattern.MULTILINE).matcher(query);
		meta.put("count", "0");
		if(m.find()){
			meta.put("count", m.group(1));
		}
		
		//
		// get title
		m=Pattern.compile("#title:([^\\n]*)",Pattern.DOTALL | Pattern.MULTILINE).matcher(query);
		if(m.find()){
			meta.put("title", m.group(1));
		}
		return meta;		
	}

}
