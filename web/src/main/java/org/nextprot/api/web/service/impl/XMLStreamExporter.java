package org.nextprot.api.web.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.PrettyPrinter;
import org.nextprot.api.web.NXVelocityContext;

/**
 * Export entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 * Daniel added pretty print for xml and xml header
 */
public class XMLStreamExporter extends NPStreamExporter {

    private final Template template;

    public XMLStreamExporter() {
        template = velocityConfig.getVelocityEngine().getTemplate("entry.xml.vm");
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {
        streamWithVelocityTemplate(template, entryName, writer, viewName);
    }

    /**
     * Will add pretty print, instead of simple template merge
     * @throws IOException 
     */
    @Override
    protected void handleMerge(Template template, VelocityContext context, Writer writer) throws IOException {
    	writePrettyXml(template, context, writer);
    }
    
    
    protected void writePrettyXml(Template template, VelocityContext context, Writer writer) throws IOException{
    	
    	String prettyXml = null;
        try( //try with resources will be closed automatically in the finally block without having to declare
        		
        		ByteArrayOutputStream out = new ByteArrayOutputStream();
        		Writer auxWriter = new PrintWriter(out);
    		) {

            template.merge(context, auxWriter);
            auxWriter.flush();
            prettyXml = PrettyPrinter.getPrettyXml(out.toString());

        }
        writer.write(prettyXml);

    }


    @Override
    protected void writeHeader(Writer writer, Map<String, Object> params) throws IOException {
        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("export-header.xml.vm");
        headerTemplate.merge(new NXVelocityContext(params), writer);

        Template releaseContentTemplate = velocityConfig.getVelocityEngine().getTemplate("release-contents.xml.vm");
        writePrettyXml(releaseContentTemplate, new NXVelocityContext(params), writer);
    	writer.write("</header>");
    	writer.write("<entry-list>");

    }

    protected void writeFooter(Writer writer) throws IOException {
        Template exportTemplate = velocityConfig.getVelocityEngine().getTemplate("export-footer.xml.vm");
		exportTemplate.merge(new NXVelocityContext(), writer);
    }
}
