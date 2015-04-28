package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 */
class XMLStreamExporter extends AbstractStreamExporter {

    private final VelocityConfig velocityConfig;

    public XMLStreamExporter(Writer writer, FluentEntryService fluentEntryService, VelocityConfig velocityConfig) {

        super(NPFileFormat.XML, writer, fluentEntryService);

        this.velocityConfig = velocityConfig;
    }

    @Override
    protected void exportStream(String entryName, String viewName) throws IOException {

        Template template = velocityConfig.getVelocityEngine().getTemplate("entry.xml.vm");

        streamWithVelocityTemplate(template, entryName, viewName);
    }

    protected void writeHeader() throws IOException {

        writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
        //writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://dl.dropboxusercontent.com/u/2037400/nextprot-export.xsd\">\n");
        writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        writer.write("<entry-list>\n");
        writer.flush();
    }

    protected void writeFooter() throws IOException {

        writer.write("</entry-list>\n");
        writer.write("</nextprot-export>\n");
    }
}
