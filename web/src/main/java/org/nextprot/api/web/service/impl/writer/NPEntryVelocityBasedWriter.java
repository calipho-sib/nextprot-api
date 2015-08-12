package org.nextprot.api.web.service.impl.writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.ApplicationContextProvider;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;

/**
 * A base class for velocity-based stream exporter
 *
 * @author fnikitin
 */
public abstract class NPEntryVelocityBasedWriter extends NPEntryWriter<Writer> {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected EntryBuilderService entryBuilderService;
    protected VelocityConfig velocityConfig;
    private final Template template;

    public NPEntryVelocityBasedWriter(Writer writer, String templateName) {

        super(writer);

        entryBuilderService = applicationContext.getBean(EntryBuilderService.class);
        velocityConfig = applicationContext.getBean(VelocityConfig.class);
        template = velocityConfig.getVelocityEngine().getTemplate(templateName);
    }

    public void setEntryBuilderService(EntryBuilderService entryBuilderService) {
        this.entryBuilderService = entryBuilderService;
    }

    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        streamWithVelocityTemplate(entryName, viewName);
    }

    final void streamWithVelocityTemplate(String entryName, String viewName, String... otherViewNames) throws IOException {

    	EntryConfig entryConfig = EntryConfig.newConfig(entryName);

    	entryConfig.with(viewName);

        for (String otherName : otherViewNames){
        	entryConfig.with(otherName);
        }

        Entry entry = entryBuilderService.build(entryConfig);

        handleEntry(entry);
        handleTemplateMerge(template, new NXVelocityContext(entry));
    }

    protected void handleTemplateMerge(Template template, VelocityContext context) throws IOException {

        template.merge(context, stream);
    }

    protected void handleEntry(Entry entry) { }
}
