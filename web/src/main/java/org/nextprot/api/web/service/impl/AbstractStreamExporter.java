package org.nextprot.api.web.service.impl;

import com.google.common.base.Preconditions;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

abstract class AbstractStreamExporter {

    protected FluentEntryService fluentEntryService;

    private final NPFileFormat format;
    protected final Writer writer;

    public AbstractStreamExporter(NPFileFormat format, Writer writer, FluentEntryService fluentEntryService) {

        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(writer);
        Preconditions.checkNotNull(fluentEntryService);

        this.writer = writer;
        this.format = format;
        this.fluentEntryService = fluentEntryService;
    }

    public void export(Collection<String> accessions, String viewName) throws IOException {

        writeHeader();

        if (accessions != null) {

            for (String acc : accessions) {
                exportStream(acc, viewName);
                writer.flush();
            }
        }

        writeFooter();
    }

    public NPFileFormat getFormat() {

        return format;
    }

    protected abstract void exportStream(String entryName, String viewName) throws IOException;

    /** Write header to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeHeader() throws IOException {}

    /** Write footer to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeFooter() throws IOException {}

    protected void streamWithVelocityTemplate(Template template, String entryName, String viewName, String... otherViewNames) {

        FluentEntryService.FluentEntry fluentEntry = fluentEntryService.newFluentEntry(entryName);

        fluentEntry.buildWithView(viewName);

        for (String otherName : otherViewNames)
            fluentEntry.buildWithView(otherName);

        VelocityContext context = new VelocityContext();
        context.put("entry", fluentEntry.build());
        context.put("StringUtils", StringUtils.class);
        context.put("NXUtils", NXVelocityUtils.class);

        template.merge(context, writer);
    }

    public static AbstractStreamExporter valueOf(NPFileFormat format, Writer writer, FluentEntryService fluentEntryService, VelocityConfig velocityConfig) throws IOException {

        switch (format) {

            case XML:
                return new XMLStreamExporter(writer, fluentEntryService, velocityConfig);
            case JSON:
                return new JSONStreamExporter(writer, fluentEntryService);
            case FASTA:
                return new FastaStreamExporter(writer, fluentEntryService, velocityConfig);
            case PEFF:
                return new PeffStreamExporter(writer, fluentEntryService, velocityConfig);
            default:
                throw new NextProtException("Format not yet supported");
        }
    }
}
