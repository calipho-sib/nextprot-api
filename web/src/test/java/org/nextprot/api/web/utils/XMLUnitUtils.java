package org.nextprot.api.web.utils;

import java.io.IOException;
import java.util.HashMap;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUnitUtils {

	public static NodeList getMatchingNodes(String xmlContent, String xpathExpression) throws SAXException, IOException, XpathException {

		Document d = XMLUnit.buildControlDocument(xmlContent);
		NamespaceContext ctx = new SimpleNamespaceContext(new HashMap<>());
		XpathEngine engine = XMLUnit.newXpathEngine();
		engine.setNamespaceContext(ctx);

		return engine.getMatchingNodes(xpathExpression, d);

	}
}
