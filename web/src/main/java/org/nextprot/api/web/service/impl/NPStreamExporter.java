package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.web.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public abstract class NPStreamExporter {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected FluentEntryService fluentEntryService;
    protected VelocityConfig velocityConfig;

    public NPStreamExporter() {

        this.fluentEntryService = (FluentEntryService) applicationContext.getBean("fluentEntryService");
        this.velocityConfig = (VelocityConfig) applicationContext.getBean("velocityConfig");
    }

    public void export(Collection<String> accessions, Writer writer, String viewName) throws IOException {

        writeHeader(writer);

        if (accessions != null) {

            for (String acc : accessions) {
                exportStream(acc, writer, viewName);
                writer.flush();
            }
        }

        writeFooter(writer);
    }

    protected abstract void exportStream(String entryName, Writer writer, String viewName) throws IOException;

    /** Write header to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeHeader(Writer writer) throws IOException {}

    /** Write footer to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeFooter(Writer writer) throws IOException {}

    protected void streamWithVelocityTemplate(Template template, String entryName, Writer writer, String viewName, String... otherViewNames) {

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
}
