package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;

/**
 * A base class for velocity-based stream writers
 *
 * @author fnikitin
 */
public abstract class EntryVelocityBasedStreamWriter extends EntryStreamWriter<Writer> {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected EntryBuilderService entryBuilderService;
    protected EntryReportStatsService entryReportStatsService;
    protected VelocityConfig velocityConfig;
    private final Template template;
    private final String viewName;

    public EntryVelocityBasedStreamWriter(Writer writer, String templateName, String viewName) {

        super(writer);

        Preconditions.checkNotNull(templateName);
        Preconditions.checkNotNull(viewName);

        entryBuilderService = applicationContext.getBean(EntryBuilderService.class);
        entryReportStatsService = applicationContext.getBean(EntryReportStatsService.class);
        velocityConfig = applicationContext.getBean(VelocityConfig.class);
        template = velocityConfig.getVelocityEngine().getTemplate(templateName);

        this.viewName = viewName;
    }

    public void setEntryBuilderService(EntryBuilderService entryBuilderService) {
        this.entryBuilderService = entryBuilderService;
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, viewName);
    }

    final void streamWithVelocityTemplate(String entryName, String... otherViewNames) throws IOException {

    	EntryConfig entryConfig = EntryConfig.newConfig(entryName);

    	entryConfig.with(viewName);

        for (String otherName : otherViewNames){
        	entryConfig.with(otherName);
        }

        Entry entry = entryBuilderService.build(entryConfig);

        handleTemplateMerge(template, newNXVelocityContext(entry));
    }

    protected void handleTemplateMerge(Template template, VelocityContext context) throws IOException {

        template.merge(context, getStream());
    }

    protected NXVelocityContext newNXVelocityContext(Entry entry) {

        return new NXVelocityContext(entry);
    }
}
