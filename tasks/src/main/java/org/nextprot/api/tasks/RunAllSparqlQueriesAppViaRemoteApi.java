package org.nextprot.api.tasks;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nextprot.api.user.domain.UserQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RunAllSparqlQueriesAppViaRemoteApi {



	/**
	 * Run all SPARQL queries that are public (excludes *.unpub files and snorql-only ones) 
	 * unless queries to be run are specified in args
	 * 
	 * @param args 
	 * arg1 is optional: api server name, example: http://build-api.nextprot.org
	 * arg2 is optional: comma separated list of query ids, example NXQ_00001,NXQ_00002
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length>0 && (args[0]=="-h" || args[0]=="--help") ) {
			System.out.println("Usage:");
			System.out.println("arg1 is optional: api server name, example: http://build-api.nextprot.org - default: http://localhost:8080/nextprot-api-web");
			System.out.println("arg2 is optional: comma separated list of specific query ids t obe run, example NXQ_00001,NXQ_00002");
			System.exit(0);
		}
		
		String server = "http://localhost:8080/nextprot-api-web";
		if (args.length>0) server = args[0];
		Map<String,String> queryMap = new HashMap<String,String>();
		if (args.length==2) {
			String[] ids = args[1].split(",");
			for (int i=0;i<ids.length;i++) queryMap.put(ids[i], null);
		}
		List<UserQuery> queries = getSparqlQueries(server);
		Date d1 = new Date(System.currentTimeMillis());
		
		int exceptionCount=0;
		int zeroResultsCount=0;
		int cnt=0;
		for (UserQuery q : queries) {

			// ---------------------------------------------------------------------------
			// if second arg is defined then skip queries that are not in queryMap 
			// ---------------------------------------------------------------------------
			if (args.length==2  && ! queryMap.containsKey(q.getPublicId())) continue; 
			// ---------------------------------------------------------------------------
			cnt++;
			long start = System.currentTimeMillis();
			int resultsCount = 0;
			System.out.println("Running query: " + q.getPublicId());
			ObjectMapper mapper = new ObjectMapper();
			URL runQueryUrl = new URL(server + "/sparql/run.json?queryId=" + q.getPublicId());
			Map<String,Object> result = mapper.readValue(runQueryUrl, new TypeReference<HashMap<String,Object>>() {});
			if (result.containsKey("error")) exceptionCount++;
			if (result.containsKey("entryCount") && (Integer)result.get("entryCount") == 0) zeroResultsCount++;
			for (Map.Entry<String,Object> e: result.entrySet()) System.out.println(e.getKey() + ": " + e.getValue());
			System.out.println();
		}

		System.out.println("Summary");
		System.out.println("Started at       : " + d1);
		System.out.println("Ended at         : " + new Date(System.currentTimeMillis()));
		System.out.println("Run query count  : " + cnt );
		System.out.println("OK count         : " + (cnt - exceptionCount - zeroResultsCount) );
		System.out.println("Exception count  : " + exceptionCount );
		System.out.println("ZeroResult count : " + zeroResultsCount );
		
	}

	
	private static List<UserQuery> getSparqlQueries(String server) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		URL queriesUrl = new URL(server + "/queries.json");
		return mapper.readValue(queriesUrl, new TypeReference<List<UserQuery>>() {});

	}

}
