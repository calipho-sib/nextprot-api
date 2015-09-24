package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLPrettyPrinter {

    private static final TransformerFactory TRANSFORMER_FACTORY;
    private static final String INDENTATION = "    ";

    private final Transformer transformer;

    static {
        TRANSFORMER_FACTORY = TransformerFactory.newInstance();
        TRANSFORMER_FACTORY.setAttribute("indent-number", INDENTATION.length());
    }

    public XMLPrettyPrinter() throws TransformerConfigurationException {

        transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    public String prettify(String xmlStr) throws TransformerException {

        return prettify(xmlStr, 0);
    }

    public String prettify(String xmlStr, int level) throws TransformerException {

        StreamResult result = new StreamResult(new StringWriter());
        StreamSource source = new StreamSource(new StringReader(removeInitialIndentation(xmlStr)));
        transformer.transform(source, result);
        return indent(result.getWriter().toString(), level);
    }

    private static String removeInitialIndentation(String input) {

        StringBuilder sb = new StringBuilder();
        String[] lines = input.split("\n");
        for (String line : lines) {
            sb.append(line.trim());
        }

        return sb.toString();
    }

    private static String indent(String input, int level) {

        StringBuilder sb = new StringBuilder();
        String[] lines = input.split("\n");
        String indentation = newIndentation(level);
        for (String line : lines) {
            sb.append(indentation);
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String newIndentation(int level) {

        Preconditions.checkArgument(level>=0);

        StringBuilder sb = new StringBuilder();

        for(int i=0 ; i<level ;i++) {
            sb.append(INDENTATION);
        }

        return sb.toString();
    }
}
