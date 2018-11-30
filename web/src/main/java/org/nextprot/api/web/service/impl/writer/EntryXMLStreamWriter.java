package org.nextprot.api.web.service.impl.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.XMLPrettyPrinter;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Streams entries in XML format
 *
 * Created by fnikitin on 28/04/15.
 * Daniel added pretty print for xml and xml header
 */
public class EntryXMLStreamWriter extends EntryVelocityBasedStreamWriter {

    private static final Log LOGGER = LogFactory.getLog(EntryXMLStreamWriter.class);
    private static final String UTF_8 = "UTF-8";

    private final XMLPrettyPrinter XMLPrettyPrinter;

    private final ByteArrayOutputStream tmpOut;
    private final Writer tmpWriter;

    public EntryXMLStreamWriter(OutputStream os, String viewName, EntryBuilderService entryBuilderService,
                                EntryReportStatsService entryReportStatsService,
                                VelocityConfig velocityConfig) throws IOException {

        this(new OutputStreamWriter(os, UTF_8), viewName, entryBuilderService, entryReportStatsService, velocityConfig);
    }

    public EntryXMLStreamWriter(Writer writer, String viewName, EntryBuilderService entryBuilderService,
                                EntryReportStatsService entryReportStatsService,
                                VelocityConfig velocityConfig) {

        super(writer, "entry.xml.vm", viewName, entryBuilderService, entryReportStatsService, velocityConfig);

        try {
            XMLPrettyPrinter = new XMLPrettyPrinter();
            tmpOut = new ByteArrayOutputStream();
            tmpWriter = new PrintWriter(tmpOut);
        } catch (TransformerConfigurationException e) {

            throw new NextProtException("cannot instanciate NPEntryXMLStreamWriter: "+ e.getMessage());
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
    private void writePrettyXml(Template template, VelocityContext context, int currentLevel) throws IOException {

        String prettyXml;

        template.merge(context, tmpWriter);
        tmpWriter.flush();
        try {
            prettyXml = XMLPrettyPrinter.prettify(tmpOut.toString(), currentLevel);
        } catch (TransformerException e) {
            LOGGER.warn(e.getMessage());
            prettyXml = tmpOut.toString();
        }
        tmpOut.reset();

        getStream().write(prettyXml);
    }

    @Override
    protected void writeHeader(Map<String, Object> infos) throws IOException {

        int entryNum = (int) infos.get(EntryStreamWriter.getEntryCountKey());
        ReleaseInfoVersions releaseInfoVersions = (ReleaseInfoVersions) infos.get(EntryStreamWriter.getReleaseInfoKey());
        ReleaseInfoDataSources releaseInfoDataSources = (ReleaseInfoDataSources) infos.get(EntryStreamWriter.getReleaseDataSourcesKey());

        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("export-header.xml.vm");
        headerTemplate.merge(new NXVelocityContext(entryNum, releaseInfoVersions), getStream());

        Template releaseContentTemplate = velocityConfig.getVelocityEngine().getTemplate("release-contents.xml.vm");
        writePrettyXml(releaseContentTemplate, new NXVelocityContext(entryNum, releaseInfoVersions, releaseInfoDataSources), 2);
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
