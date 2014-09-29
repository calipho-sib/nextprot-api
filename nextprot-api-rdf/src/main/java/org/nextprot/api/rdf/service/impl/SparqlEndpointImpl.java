package org.nextprot.api.rdf.service.impl;

import static org.nextprot.api.commons.utils.RdfUtils.RDF_PREFIXES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nextprot.api.commons.utils.FileUtils;
import org.nextprot.api.commons.utils.RdfUtils;
import org.nextprot.api.rdf.domain.TripleInfo;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

@Service
public class SparqlEndpointImpl implements SparqlEndpoint {

	private static final List<String> RDF_TYPES_TO_EXCLUDE = Arrays.asList(":childOf", "rdf:Property");

	private String url;
	private String timeout;

	public String getUrl() {
		return url;
	}

	@Value("${sparql.url}")
	public void setUrl(String url) {
		this.url = url;
	}

	public String getTimeout() {
		return timeout;
	}

	@Value("${sparql.timeout}")
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
	public QueryExecution queryExecution(String query) {

		QueryEngineHTTP qExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(url, query);
		qExec.addParam("timeout", timeout);
		return qExec;
	}
	
	
	

	@Override
	@Cacheable("rdftypefullall")
	public Set<String> getAllRdfTypesNames() {
		Set<String> result = new TreeSet<String>();
		System.err.println("Sending request to ...." + getUrl());
		String query = FileUtils.readResourceAsString("/sparql/alldistincttypes.rq");
		QueryExecution qExec = queryExecution(RdfUtils.RDF_PREFIXES + query);
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

	@Override
	@Cacheable("rdftypename")
	public Map<String, String> getRdfTypeProperties(String rdfType) {
		Map<String, String> properties = new HashMap<String, String>();
		String queryBase = FileUtils.readResourceAsString("/sparql/typenames.rq");
		String query = RDF_PREFIXES;
		query += queryBase.replace(":SomeRdfType", rdfType);
		QueryExecution qExec = queryExecution(query);
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

	@Override
	@Cacheable("rdftypetriple")
	public List<TripleInfo> getTripleInfoList(String rdfType) {
		String queryBase = RDF_PREFIXES + FileUtils.readResourceAsString("sparql/typepred.rq");
		Set<TripleInfo> tripleList = new TreeSet<TripleInfo>();
		String query = RDF_PREFIXES;
		query += queryBase.replace(":SomeRdfType", rdfType);
		QueryExecution qExec = queryExecution(query);
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

	@Override
	@Cacheable("rdftypevalues")
	public Set<String> getRdfTypeValues(String rdfTypeInfoName, int limit) {
		
		Set<String> values = new TreeSet<String>();
		String queryBase = FileUtils.readResourceAsString("/sparql/typevalues.rq");
		String query = RDF_PREFIXES;
		query += queryBase.replace(":SomeRdfType", rdfTypeInfoName).replace(":LimitResults", String.valueOf(limit));
		QueryExecution qExec = queryExecution(query);
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

	@Override
	@Cacheable("rdftypetriplevalues")
	public Set<String> getValuesForTriple(String rdfTypeName, String predicate, int limit) {

		Set<String> values = new TreeSet<String>();
		String queryBase = FileUtils.readResourceAsString("/sparql/getliteralvalues.rq");
		String query = RDF_PREFIXES;
		query += queryBase.replace(":SomeRdfType", rdfTypeName).replace(":SomePredicate", predicate).replace(":LimitResults", String.valueOf(limit));

		QueryExecution qExec = queryExecution(query);
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
	
	
	private static Object getDataFromSolutionVar(QuerySolution sol, String var) {
		return getDataFromSolutionVar(sol, var, false);
	}

	private static Object getDataFromSolutionVar(QuerySolution sol, String var, boolean useQuotes) {
		RDFNode n = sol.get(var);
		if (n == null)
			return "";
		RDFBasicVisitor rdfVisitor = new RDFBasicVisitor();
		rdfVisitor.setSurroundLiteralStringWithQuotes(useQuotes);
		return n.visitWith(rdfVisitor);
	}

	private static String getObjectTypeFromSample(QuerySolution sol, String objSample) {
		try {
			Literal lit = sol.getLiteral(objSample);
			String typ = lit.getDatatypeURI();
			return RdfUtils.getPrefixedNameFromURI(typ);

		} catch (Exception e) {
			System.err.println("Failed for " + objSample);
			return RdfUtils.BLANK_OBJECT_TYPE;
		}

	}



}
