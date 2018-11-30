package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;

/**
 * A base class for velocity-based stream writers
 *
 * @author fnikitin
 */
public abstract class EntryVelocityBasedStreamWriter extends EntryStreamWriter<Writer> {

    protected EntryBuilderService entryBuilderService;
    protected EntryReportStatsService entryReportStatsService;
    protected VelocityConfig velocityConfig;
    private final Template template;
    private final String viewName;

    public EntryVelocityBasedStreamWriter(Writer writer, String templateName, String viewName,
                                          EntryBuilderService entryBuilderService,
                                          EntryReportStatsService entryReportStatsService,
                                          VelocityConfig velocityConfig) {

        super(writer);

        Preconditions.checkNotNull(templateName);
        Preconditions.checkNotNull(viewName);

        this.entryBuilderService = entryBuilderService;
        this.entryReportStatsService = entryReportStatsService;
        this.velocityConfig = velocityConfig;
        template = velocityConfig.getVelocityEngine().getTemplate(templateName);

        this.viewName = viewName;
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
