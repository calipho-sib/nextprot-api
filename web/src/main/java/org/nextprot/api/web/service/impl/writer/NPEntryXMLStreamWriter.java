package org.nextprot.api.web.service.impl.writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.XMLPrettyPrinter;
import org.nextprot.api.web.NXVelocityContext;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Streams entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 * Daniel added pretty print for xml and xml header
 */
public class NPEntryXMLStreamWriter extends NPEntryVelocityBasedStreamWriter {

    private final XMLPrettyPrinter XMLPrettyPrinter;

    private final ByteArrayOutputStream tmpOut;
    private final Writer tmpWriter;

    public NPEntryXMLStreamWriter(Writer writer, String viewName) {

        super(writer, "entry.xml.vm", viewName);

        try {
            XMLPrettyPrinter = new XMLPrettyPrinter();
            tmpOut = new ByteArrayOutputStream();
            tmpWriter = new PrintWriter(tmpOut);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new NextProtException("internal error: cannot instanciate NPEntryXMLStreamWriter");
        }
    }

    /**
     * Will add pretty print, instead of simple template merge
     * @throws IOException 
     */
    @Override
    protected void handleTemplateMerge(Template template, VelocityContext context) throws IOException {
    	writePrettyXml(template, context, 2);
    }

    /**
     *
     * @param template
     * @param context
     * @param currentLevel level 0 is root level
     * @throws IOException
     */
    private void writePrettyXml(Template template, VelocityContext context, int currentLevel) throws IOException{

        String prettyXml;

        template.merge(context, tmpWriter);
        tmpWriter.flush();
        try {
            prettyXml = XMLPrettyPrinter.prettify(tmpOut.toString(), currentLevel);
        } catch (TransformerException e) {
            e.printStackTrace();
            prettyXml = tmpOut.toString();
        }
        tmpOut.reset();

        getStream().write(prettyXml);
    }

    @Override
    protected void writeHeader(Map<String, Object> params) throws IOException {
        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("export-header.xml.vm");
        headerTemplate.merge(new NXVelocityContext(params), getStream());

        Template releaseContentTemplate = velocityConfig.getVelocityEngine().getTemplate("release-contents.xml.vm");
        writePrettyXml(releaseContentTemplate, new NXVelocityContext(params), 2);
        getStream().write("    </header>\n");
        getStream().write("    <entry-list>\n");
    }

    @Override
    protected void writeFooter() throws IOException {
        Template exportTemplate = velocityConfig.getVelocityEngine().getTemplate("export-footer.xml.vm");
		exportTemplate.merge(new NXVelocityContext(), getStream());
    }

    @Override
    public void close() throws IOException {

        tmpOut.close();
        tmpWriter.close();
    }
}
