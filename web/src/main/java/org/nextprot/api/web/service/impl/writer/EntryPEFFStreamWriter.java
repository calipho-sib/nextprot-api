package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Streams entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class EntryPEFFStreamWriter extends EntryOutputStreamWriter {

    private final EntryReportStatsService entryReportStatsService;
    private final Map<String, Entry> cachedEntries;

    public EntryPEFFStreamWriter(OutputStream os) throws IOException {

        super(os);

        this.entryReportStatsService = applicationContext.getBean(EntryReportStatsService.class);
        cachedEntries = new HashMap<>();
    }

    @Override
    protected void writeHeader(Map<String, Object> infos) throws IOException {

        int isoformNumber = (int)infos.get(ISOFORM_COUNT);
        String description = (String) infos.getOrDefault(DESCRIPTION, "");

        StringBuilder sb = new StringBuilder();

        sb.append("# PEFF 1.0").append(StringUtils.CR_LF)
                .append("# //").append(StringUtils.CR_LF)
                .append("# DbName=neXtProt: ").append(description).append(StringUtils.CR_LF)
                .append("# DbSource=https://www.nextprot.org").append(StringUtils.CR_LF);

        if (infos.containsKey(RELEASE_INFO))
                sb.append("# DbVersion=").append(((ReleaseInfoVersions)infos.get(RELEASE_INFO)).getDatabaseRelease()).append(StringUtils.CR_LF);

        sb.append("# Prefix=nxp").append(StringUtils.CR_LF)
                .append("# NumberOfEntries=").append(isoformNumber).append(StringUtils.CR_LF)
                .append("# SequenceType=AA").append(StringUtils.CR_LF)
                .append("# GeneralComment=Copyrighted by the SIB Swiss Institute of Bioinformatics").append(StringUtils.CR_LF)
                .append("# GeneralComment=Distributed under the Creative Commons Attribution 4.0 International Public License (CC BY 4.0)").append(StringUtils.CR_LF)
                .append("# //").append(StringUtils.CR_LF);

        getStream().write(sb.toString().getBytes());
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        Map<String, String> isoformToPEFF = entryReportStatsService.reportIsoformPeffHeaders(entryName);

        StringBuilder sb = new StringBuilder();

        for (Isoform isoform : cachedEntries.get(entryName).getIsoforms()) {

            sb.append(">")
                    .append(isoformToPEFF.get(isoform.getIsoformAccession())).append(StringUtils.CR_LF)
                    .append(StringUtils.wrapText(isoform.getSequence(), 60)).append(StringUtils.CR_LF);
        }
        getStream().write(sb.toString().getBytes());
    }

    @Override
    public void write(Collection<String> entryAccessions, Map<String, Object> infos) throws IOException {

        cacheBuiltEntries(entryAccessions);

        // isoform count information has to be computed before streaming all entries
        infos.put(ISOFORM_COUNT, cachedEntries.values().stream()
                .mapToInt(e -> e.getIsoforms().size())
                .sum());

        // do the streaming
        super.write(entryAccessions, infos);

        removeBuildEntriesFromCache();
    }

    private void cacheBuiltEntries(Collection<String> entryAccessions) {

        for (String entryAccession : entryAccessions) {

            Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withTargetIsoforms());
            cachedEntries.put(entryAccession, entry);
        }
    }

    private void removeBuildEntriesFromCache() {

        cachedEntries.clear();
    }
}
