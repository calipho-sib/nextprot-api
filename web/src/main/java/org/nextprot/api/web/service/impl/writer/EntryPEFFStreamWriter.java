package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Streams entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class EntryPEFFStreamWriter extends EntryOutputStreamWriter {

    private final EntryReportService entryReportService;

    public EntryPEFFStreamWriter(OutputStream os) throws IOException {

        super(os);

        this.entryReportService = applicationContext.getBean(EntryReportService.class);
    }

    @Override
    protected void writeHeader(int entryNum, ReleaseInfo releaseInfo, String description) throws IOException {

        StringBuilder sb = new StringBuilder();

        sb
                .append("# PEFF 1.0").append(StringUtils.CR_LF)
                .append("# DbName=neXtProt: ").append(description).append(StringUtils.CR_LF)
                .append("# DbSource=https://www.nextprot.org").append(StringUtils.CR_LF)
                .append("# DbVersion=").append(releaseInfo.getDatabaseRelease()).append(StringUtils.CR_LF)
                .append("# Prefix=nxp").append(StringUtils.CR_LF)
                .append("# NumberOfEntries=").append(entryNum).append(StringUtils.CR_LF)
                .append("# SequenceType=AA").append(StringUtils.CR_LF)
                .append("# //").append(StringUtils.CR_LF)
        ;

        getStream().write(sb.toString().getBytes());
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        Map<String, String> isoformToPEFF = entryReportService.reportIsoformPeffHeaders(entryName);

        EntryConfig entryConfig = EntryConfig.newConfig(entryName);
        entryConfig.withTargetIsoforms();

        Entry entry = entryBuilderService.build(entryConfig);

        StringBuilder sb = new StringBuilder();

        for (Isoform isoform : entry.getIsoforms()) {

            sb.append(">")
                    .append(isoformToPEFF.get(isoform.getIsoformAccession())).append(StringUtils.CR_LF)
                    .append(StringUtils.wrapText(isoform.getSequence(), 60)).append(StringUtils.CR_LF);
        }
        getStream().write(sb.toString().getBytes());
    }

    /*public static int countIsoforms(List<Entry> entries) {

        return entries.stream()
                .mapToInt(e -> e.getIsoforms().size())
                .sum();
    }*/
}
