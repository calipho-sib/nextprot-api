package org.nextprot.api.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class PrettyPrinter {


	public static String getPrettyXml(String xmlStr) {
		
		try {
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			StreamSource source = new StreamSource(new StringReader(removeInitialIndentation(xmlStr)));
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			return xmlString;

		} catch (TransformerException e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
			return xmlStr;
		}
	}
	
	
	private static String removeInitialIndentation(String input) {

		StringBuilder sb = new StringBuilder();
		String[] lines = input.split("\n");
		for (String line : lines) {
			sb.append(line.trim());
		}

		return sb.toString();

	}


}
