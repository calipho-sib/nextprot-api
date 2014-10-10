package org.nextprot.api.rdf.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.rdf.domain.RdfConstants;
import org.nextprot.api.rdf.domain.TripleInfo;
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

@Service
public class RdfHelpCacheableServiceImpl  {

	private static final Log LOGGER = LogFactory.getLog(SparqlEndpointImpl.class);

	private static final List<String> RDF_TYPES_TO_EXCLUDE = Arrays.asList(":childOf", "rdf:Property");

	private @Autowired SparqlDictionary sparqlDictionary = null;
	private @Autowired SparqlService sparqlService = null;

	@Cacheable("rdftypefullall")
	public Set<String> getAllRdfTypesNames() {
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

	@Cacheable("rdftypename")
	public Map<String, String> getRdfTypeProperties(String rdfType) {
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

	@Cacheable("rdftypetriple")
	public List<TripleInfo> getTripleInfoList(String rdfType) {
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

	@Cacheable("rdftypevalues")
	public Set<String> getRdfTypeValues(String rdfTypeInfoName, int limit) {
		
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

	@Cacheable("rdftypetriplevalues")
	public Set<String> getValuesForTriple(String rdfTypeName, String predicate, int limit) {

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
