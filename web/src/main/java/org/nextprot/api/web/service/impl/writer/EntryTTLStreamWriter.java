package org.nextprot.api.web.service.impl.writer;

import org.apache.velocity.Template;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.web.NXVelocityContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by dteixeira
 */
public class EntryTTLStreamWriter extends EntryVelocityBasedStreamWriter {

    public EntryTTLStreamWriter(Writer writer, String viewName) {
        super(writer, "turtle/entry.ttl.vm", viewName);
    }
    
    public EntryTTLStreamWriter(OutputStream os, String viewName) {
        this(new OutputStreamWriter(os), viewName);
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
