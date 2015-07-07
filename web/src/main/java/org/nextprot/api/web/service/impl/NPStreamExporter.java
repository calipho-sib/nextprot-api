package org.nextprot.api.web.service.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.service.impl.EntryBuilderServiceImpl;
import org.nextprot.api.web.ApplicationContextProvider;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

public abstract class NPStreamExporter {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected EntryBuilderServiceImpl fluentEntryService;
    protected VelocityConfig velocityConfig;
    protected TerminologyService terminologyService;
    
    public NPStreamExporter() {

        this.fluentEntryService = applicationContext.getBean(EntryBuilderServiceImpl.class);
        this.velocityConfig = applicationContext.getBean(VelocityConfig.class);
    }

    protected void setTerminologyService(TerminologyService ts) {

        terminologyService = ts;
    }

    public void export(Collection<String> accessions, Writer writer, String viewName, Map<String,Object> map) throws IOException {

        writeHeader(writer, map);

        if (accessions != null) {

            for (String acc : accessions) {
                exportStream(acc, writer, viewName);
                writer.flush();
            }
        }

        writeFooter(writer);
        writer.flush();
    }

    protected abstract void exportStream(String entryName, Writer writer, String viewName) throws IOException;

    /** Write header to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeHeader(Writer writer, Map<String, Object> params) throws IOException {}

    /** Write footer to the output stream (supposed to be overriden by sub classes if needed) */
    protected void writeFooter(Writer writer) throws IOException {}

    protected void streamWithVelocityTemplate(Template template, String entryName, Writer writer, String viewName, String... otherViewNames) throws IOException {

    	EntryConfig entryConfig = EntryConfig.newConfig(entryName);

    	entryConfig.with(viewName);

        for (String otherName : otherViewNames)
        	entryConfig.with(otherName);

        Entry entry = fluentEntryService.build(entryConfig);

        handleEntry(entry);
        handleMerge(template, new NXVelocityContext(entry), writer);

    }

    protected void handleMerge(Template template, VelocityContext context, Writer writer) throws IOException {
        template.merge(context, writer);
    }
    
    protected void handleEntry(Entry entry) {

    }
}
