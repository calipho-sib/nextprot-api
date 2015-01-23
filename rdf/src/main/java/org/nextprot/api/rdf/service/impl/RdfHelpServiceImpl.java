package org.nextprot.api.rdf.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.domain.RdfConstants;
import org.nextprot.api.rdf.domain.RdfTypeInfo;
import org.nextprot.api.rdf.domain.TripleInfo;
import org.nextprot.api.rdf.service.RdfHelpService;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.rdf.utils.RdfPrefixUtils;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

//@Lazy
@Service
public class RdfHelpServiceImpl implements RdfHelpService {

	private static final Log LOGGER = LogFactory.getLog(SparqlEndpointImpl.class);

	private List<String> completeSetOfValuesForTypes = Arrays.asList(":Source", ":Database", ":SubcellularLocation", ":NextprotTissues");
	private List<String> completeSetOfValuesForLiteral = Arrays.asList("NextprotTissues/rdfs:label", ":SubcellularLocation/rdfs:comment");

	private static final List<String> RDF_TYPES_TO_EXCLUDE = Arrays.asList(":childOf", "rdf:Property");

	private @Autowired SparqlDictionary sparqlDictionary = null;
	private @Autowired SparqlService sparqlService = null;


	private final int NUMBER_THREADS = 10;

	@Cacheable("rdfhelp")
	@Override
	public List<RdfTypeInfo> getRdfTypeFullInfoList() {

		Set<String> rdfTypesNames = getAllRdfTypesNames();
		List<Future<RdfTypeInfo>> rdfFutureTypes = new ArrayList<Future<RdfTypeInfo>>();
		List<RdfTypeInfo> rdfTypes = Collections.synchronizedList(new ArrayList<RdfTypeInfo>());

		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		for (String rdfTypeName : rdfTypesNames) {
			Future<RdfTypeInfo> futureRdfTypeInfo = executor.submit(new FillRdfTypeInfoTask(this, rdfTypeName));
			rdfFutureTypes.add(futureRdfTypeInfo);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new NextProtException(e.getLocalizedMessage());
		}

		for (Future<RdfTypeInfo> futureRdfTypeInfo : rdfFutureTypes) {
			try {
				rdfTypes.add(futureRdfTypeInfo.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		// now populate parent and parent triples of each type
		for (RdfTypeInfo rti : rdfTypes) {
			for (RdfTypeInfo parent : rdfTypes) {
				List<TripleInfo> triples = parent.findTriplesWithObjectType(rti.getTypeName());
				if (triples.size() > 0) {
					rti.addParent(parent.getTypeName());
					for (TripleInfo triple : triples)
						rti.addParentTriple(triple);
				}
			}
		}

		Map<String, RdfTypeInfo> fullMap = new HashMap<String, RdfTypeInfo>();
		for (RdfTypeInfo rti : rdfTypes) {
			fullMap.put(rti.getTypeName(), rti);
		}

		buildPathToOrigin(fullMap, fullMap.get(":Entry"), "?entry ", 0);

		return rdfTypes;
	}

	private static void buildPathToOrigin(final Map<String, RdfTypeInfo> fullMap, RdfTypeInfo currentEntry, String currentPath, int currenDepth) {
		if (currenDepth > 20)
			return;
		for (TripleInfo childTripleInfo : currentEntry.getTriples()) {
			RdfTypeInfo childTypeInfo = fullMap.get(childTripleInfo.getObjectType());
			if (childTypeInfo != null && !currentPath.contains(childTripleInfo.getPredicate()) && !childTripleInfo.getPredicate().equals(":interaction")) {
				String nextPath = currentPath + childTripleInfo.getPredicate();
				childTypeInfo.addPathToOrigin(nextPath);
				buildPathToOrigin(fullMap, childTypeInfo, nextPath + "/", currenDepth + 1);
			}
		}
	}


	@Override
	public RdfTypeInfo getRdfTypeFullInfo(String rdfTypeName) {

		RdfTypeInfo rdfTypeInfo = new RdfTypeInfo();
		rdfTypeInfo.setTypeName(rdfTypeName);

		Map<String, String> properties = getRdfTypeProperties(rdfTypeName);
		
		if(!properties.isEmpty()){
			rdfTypeInfo.setTypeName(properties.get("rdfType"));
			rdfTypeInfo.setRdfsLabel(properties.get("label"));
			rdfTypeInfo.setRdfsComment(properties.get("comment"));
			rdfTypeInfo.setInstanceCount(Integer.valueOf(properties.get("instanceCount")));
			rdfTypeInfo.setInstanceSample(properties.get("instanceSample"));
		}


		List<TripleInfo> triples = getTripleInfoList(rdfTypeInfo.getTypeName());

		Set<String> values = null;
		if (completeSetOfValuesForTypes.contains(rdfTypeInfo.getTypeName())) {
			values = getRdfTypeValues(rdfTypeName, Integer.MAX_VALUE);
		} else {
			values = getRdfTypeValues(rdfTypeName, 20);
		}

		rdfTypeInfo.setValues(values);

		for (TripleInfo triple : triples) {
			rdfTypeInfo.addTriple(triple);
			if (triple.isLiteralType() && (!triple.getObjectType().equals(RdfConstants.BLANK_OBJECT_TYPE))) {
				String typeLiteral = rdfTypeName + "/" + triple.getPredicate();
				Set<String> exampleValues = null;
				if (completeSetOfValuesForLiteral.contains(typeLiteral)) {
					exampleValues = getValuesForTriple(rdfTypeName, triple.getPredicate(), Integer.MAX_VALUE);
				} else {
					exampleValues = getValuesForTriple(rdfTypeName, triple.getPredicate(), 50);
				}

				triple.setValues(exampleValues);
			}
		}

		return rdfTypeInfo;

	}

	@Override
	public List<String> getRdfTypeValues(String rdfTypeName) {
		return new ArrayList<String>(getRdfTypeValues(rdfTypeName, Integer.MAX_VALUE));
	}
	
	
	//Task
	private static class FillRdfTypeInfoTask implements Callable<RdfTypeInfo> {

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

	private Set<String> getAllRdfTypesNames() {
		Set<String> result = new TreeSet<String>();
		String query = sparqlDictionary.getSparqlWithPrefixes("alldistincttypes");
		QueryExecution qExec = sparqlService.queryExecution(query);
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			String rdfTypeName = (String) getDataFromSolutionVar(rs.next(), "rdfType");
			if (!RDF_TYPES_TO_EXCLUDE.contains(rdfTypeName)) {
				if (!rdfTypeName.startsWith("http://") && !rdfTypeName.startsWith("owl:") && !rdfTypeName.startsWith("rdfs:Class")) {
					if (!result.contains(rdfTypeName)) {
						result.add(rdfTypeName);
					} else {
						System.out.println(rdfTypeName + " is not unique");
					}
				} else {
					System.out.println("Skipping " + rdfTypeName);
				}
			}
		}
		qExec.close();

		System.out.println("Found " + result.size());

		return result;
	}

	private Map<String, String> getRdfTypeProperties(String rdfType) {
		Map<String, String> properties = new HashMap<String, String>();
		String queryBase = sparqlDictionary.getSparqlOnly("typenames");
		String query = sparqlDictionary.getSparqlPrefixes();
		query += queryBase.replace(":SomeRdfType", rdfType);
		QueryExecution qExec = sparqlService.queryExecution(query);
		ResultSet rs = qExec.execSelect();
		if (rs.hasNext()) {
			QuerySolution sol = rs.next();
			properties.put("rdfType", (String) getDataFromSolutionVar(sol, "rdfType"));
			properties.put("label", (String) getDataFromSolutionVar(sol, "label"));
			properties.put("comment", (String) getDataFromSolutionVar(sol, "comment"));
			properties.put("instanceCount", (String) getDataFromSolutionVar(sol, "instanceCount"));
			properties.put("instanceSample", (String) getDataFromSolutionVar(sol, "instanceSample"));
		}
		qExec.close();
		return properties;
	}


	private List<TripleInfo> getTripleInfoList(String rdfType) {
		String queryBase = sparqlDictionary.getSparqlWithPrefixes("typepred");
		Set<TripleInfo> tripleList = new TreeSet<TripleInfo>();
		String query = sparqlDictionary.getSparqlOnly("prefix");
		query += queryBase.replace(":SomeRdfType", rdfType);
		QueryExecution qExec = sparqlService.queryExecution(query);
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.next();
			TripleInfo ti = new TripleInfo();
			String pred = (String) getDataFromSolutionVar(sol, "pred");
			String sspl = (String) getDataFromSolutionVar(sol, "subjSample");
			String ospl = (String) getDataFromSolutionVar(sol, "objSample", true);

			String spl = sspl + " " + pred + " " + ospl + " .";
			ti.setTripleSample(spl);
			ti.setPredicate(pred);
			ti.setSubjectType((String) getDataFromSolutionVar(sol, "subjType"));

			String objectType = (String) getDataFromSolutionVar(sol, "objType");
			if (objectType.length() == 0) {
				System.out.println(ti);
				objectType = getObjectTypeFromSample(sol, "objSample");
				ti.setLiteralType(true);
			}
			ti.setObjectType(objectType);

			ti.setTripleCount(Integer.valueOf((String) getDataFromSolutionVar(sol, "objCount")));

			tripleList.add(ti);
		}
		qExec.close();
		return new ArrayList<TripleInfo>(tripleList);

	}


	private Set<String> getRdfTypeValues(String rdfTypeInfoName, int limit) {
		
		Set<String> values = new TreeSet<String>();
		//TODO add a method with a map of named parameters in the sparql dictionary
		String queryBase = sparqlDictionary.getSparqlOnly("typevalues");
		String query = sparqlDictionary.getSparqlPrefixes();
		query += queryBase.replace(":SomeRdfType", rdfTypeInfoName).replace(":LimitResults", String.valueOf(limit));
		QueryExecution qExec = sparqlService.queryExecution(query);
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.next();
			String value = (String) getDataFromSolutionVar(sol, "value");
			if(value.startsWith("annotation:")){
				values.add("Example: " + value);
				break;
			}else {
				values.add(value);
			}
		}
		qExec.close();
		
		//Reduce the json if the list is not complete, just put a simple example
		if(values.size() == limit){
			Iterator<String> it = values.iterator();
			String sample1 = it.next();
			values.clear();
			values.add("Example: " + sample1);
		}

		return values;
	}

	private Set<String> getValuesForTriple(String rdfTypeName, String predicate, int limit) {

		Set<String> values = new TreeSet<String>();
		String queryBase = sparqlDictionary.getSparqlOnly("getliteralvalues");
		String query = sparqlDictionary.getSparqlPrefixes();
		query += queryBase.replace(":SomeRdfType", rdfTypeName).replace(":SomePredicate", predicate).replace(":LimitResults", String.valueOf(limit));

		QueryExecution qExec = sparqlService.queryExecution(query);
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.next();
			values.add((String) getDataFromSolutionVar(sol, "value"));
		}
		qExec.close();
		
		//Reduce the json if the list is not complete, just put a simple example
		//Reduce the json if the list is not complete, just put a simple example
		if(values.size() == limit){
			Iterator<String> it = values.iterator();
			String sample1 = it.next();
			values.clear();
			values.add("Example: " + sample1);
		}
		return values;
	}
	
	
	/**
	 * Private static methods
	 */
	
	
	private Object getDataFromSolutionVar(QuerySolution sol, String var) {
		return getDataFromSolutionVar(sol, var, false);
	}

	private Object getDataFromSolutionVar(QuerySolution sol, String var, boolean useQuotes) {
		RDFNode n = sol.get(var);
		if (n == null)
			return "";
		RDFBasicVisitor rdfVisitor = new RDFBasicVisitor(sparqlDictionary.getSparqlPrefixes());
		rdfVisitor.setSurroundLiteralStringWithQuotes(useQuotes);
		return n.visitWith(rdfVisitor);
	}

	private String getObjectTypeFromSample(QuerySolution sol, String objSample) {
		try {
			Literal lit = sol.getLiteral(objSample);
			String typ = lit.getDatatypeURI();
			return RdfPrefixUtils.getPrefixedNameFromURI(sparqlDictionary.getSparqlPrefixes(), typ);

		} catch (Exception e) {
			LOGGER.error("Failed for " + objSample);
			return RdfConstants.BLANK_OBJECT_TYPE;
		}

	}



}
