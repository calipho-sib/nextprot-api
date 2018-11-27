package org.nextprot.api.web.service.impl.writer;

import org.apache.velocity.Template;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by dteixeira
 */
public class EntryTTLStreamWriter extends EntryVelocityBasedStreamWriter {

    public EntryTTLStreamWriter(Writer writer, String viewName, ApplicationContext applicationContext) {
        super(writer, "turtle/entry.ttl.vm", viewName, applicationContext);
    }
    
    public EntryTTLStreamWriter(OutputStream os, String viewName, ApplicationContext applicationContext) {
        this(new OutputStreamWriter(os), viewName, applicationContext);
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {
        streamWithVelocityTemplate(entryName);
    }

    @Override
    protected void writeHeader(Map<String, Object> infos) {

        int entryNum = (int) infos.get(EntryStreamWriter.getEntryCountKey());
        ReleaseInfoVersions releaseInfoVersions = (ReleaseInfoVersions) infos.get(EntryStreamWriter.getReleaseInfoKey());

        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("turtle/prefix.ttl.vm");
        headerTemplate.merge(new NXVelocityContext(entryNum, releaseInfoVersions), getStream());
    }
}
