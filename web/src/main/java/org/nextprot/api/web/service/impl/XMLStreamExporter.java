package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 */
public class XMLStreamExporter extends NPStreamExporter {

    private final Template template;

    XMLStreamExporter() {

        template = velocityConfig.getVelocityEngine().getTemplate("entry.xml.vm");
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        streamWithVelocityTemplate(template, entryName, writer, viewName);
    }

    protected void writeHeader(Writer writer) throws IOException {

        writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
        //writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://dl.dropboxusercontent.com/u/2037400/nextprot-export.xsd\">\n");
        writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        writer.write("<entry-list>\n");
        writer.flush();
    }

    protected void writeFooter(Writer writer) throws IOException {

        writer.write("</entry-list>\n");
        writer.write("</nextprot-export>\n");
    }
}
