package org.nextprot.api.rdf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;


public class RDFBasicVisitor implements RDFVisitor {

	@Autowired private RdfPrefixService rdfPrefixService;
	
	private boolean surroundLiteralStringWithQuotes = false;
	
	public RDFBasicVisitor() {
	}

	public void setSurroundLiteralStringWithQuotes(boolean state) {
		surroundLiteralStringWithQuotes=state;
	}
	
	
	@Override
	public Object visitBlank(Resource r, AnonId id) {
		return "blank( " + id.toString() + " )";
	}

	@Override
	public Object visitLiteral(Literal l) {
		if (surroundLiteralStringWithQuotes && l.getDatatypeURI().endsWith("string")) {
			return "\"" + l.getValue() + "\"";
		} else {
			return l.getValue().toString();
		}
	}

	@Override
	public Object visitURI(Resource r, String uri) {
		return rdfPrefixService.getPrefixedNameFromURI(uri);
	}
	
	
	public String getPrefixedName(Resource r) {
		if (r==null) return null;
		String pn = rdfPrefixService.getPrefixedNameFromURI(r.getURI());
		return pn;
	}
	
	@Deprecated
	public String getPrefixedNameOld(Resource r) {
		if (r==null) return null;
		String ln = r.getLocalName(); // doesn't work if local name starts with digit !
		String ns = r.getNameSpace(); // doesn't work if local name starts with digit !
		String pn = rdfPrefixService.getPrefixFromNameSpace(ns) + ln;
		return pn;
	}
	
	
	
}
