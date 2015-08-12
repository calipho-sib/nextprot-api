package org.nextprot.api.web.service.impl.writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.PrettyPrinter;
import org.nextprot.api.web.NXVelocityContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Export entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 * Daniel added pretty print for xml and xml header
 */
public class NPEntryXMLWriter extends NPEntryVelocityBasedWriter {

    public NPEntryXMLWriter(Writer writer) {

        super(writer, "entry.xml.vm");
    }

    /**
     * Will add pretty print, instead of simple template merge
     * @throws IOException 
     */
    @Override
    protected void handleTemplateMerge(Template template, VelocityContext context) throws IOException {
    	writePrettyXml(template, context);
    }

    private void writePrettyXml(Template template, VelocityContext context) throws IOException{
    	
    	String prettyXml;
        try( //try with resources will be closed automatically in the finally block without having to declare
        		
        		ByteArrayOutputStream out = new ByteArrayOutputStream();
        		Writer auxWriter = new PrintWriter(out);
    		) {

            template.merge(context, auxWriter);
            auxWriter.flush();
            prettyXml = PrettyPrinter.getPrettyXml(out.toString());

        }
        stream.write(prettyXml);
    }

    @Override
    protected void writeHeader(Map<String, Object> params) throws IOException {
        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("export-header.xml.vm");
        headerTemplate.merge(new NXVelocityContext(params), stream);

        Template releaseContentTemplate = velocityConfig.getVelocityEngine().getTemplate("release-contents.xml.vm");
        writePrettyXml(releaseContentTemplate, new NXVelocityContext(params));
        stream.write("  </header>");
        stream.write("<entry-list>");
    }

    @Override
    protected void writeFooter() throws IOException {
        Template exportTemplate = velocityConfig.getVelocityEngine().getTemplate("export-footer.xml.vm");
		exportTemplate.merge(new NXVelocityContext(), stream);
    }
}
