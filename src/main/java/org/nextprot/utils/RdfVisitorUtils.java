package org.nextprot.utils;

import org.nextprot.api.service.impl.RDFBasicVisitor;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class RdfVisitorUtils {

	public static Object getDataFromSolutionVar(QuerySolution sol, String var) {
		return getDataFromSolutionVar(sol, var, false);
	}

	public static Object getDataFromSolutionVar(QuerySolution sol, String var, boolean useQuotes) {
		RDFNode n = sol.get(var);
		if (n == null)
			return "";
		RDFBasicVisitor rdfVisitor = new RDFBasicVisitor();
		rdfVisitor.setSurroundLiteralStringWithQuotes(useQuotes);
		return n.visitWith(rdfVisitor);
	}

	public static String getObjectTypeFromSample(QuerySolution sol, String objSample) {
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
