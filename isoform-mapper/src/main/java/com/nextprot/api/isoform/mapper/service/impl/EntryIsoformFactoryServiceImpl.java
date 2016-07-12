package com.nextprot.api.isoform.mapper.service.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.service.EntryIsoformFactoryService;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryIsoformFactoryServiceImpl implements EntryIsoformFactoryService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public EntryIsoform createsEntryIsoform(String accession) {

        Preconditions.checkNotNull(accession, "missing accession name (either entry name or isoform name)");
        Preconditions.checkNotNull(entryBuilderService);

        String entryName = parseEntryName(accession);
        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withTargetIsoforms().withOverview());

        if (!isIsoformAccession(accession)) {
            return new EntryIsoform(accession, entry, getCanonicalIsoform(entry));
        }
        return new EntryIsoform(accession, entry, EntryIsoform.getIsoformByName(entry, accession));
    }

    private static String parseEntryName(String accession) {

        // isoform accession
        if (isIsoformAccession(accession)) {
            int dashPosition = accession.indexOf("-");
            return accession.substring(0, dashPosition);
        }
        // entry accession
        return accession;
    }

    private static boolean isIsoformAccession(String accession) {

        return accession.contains("-");
    }

    /**
     * Return the canonical isoform of the given entry
     */
    private Isoform getCanonicalIsoform(Entry entry) {

        for (Isoform iso: entry.getIsoforms()) {
            if (iso.isCanonicalIsoform()) return iso;
        }
        return null;
    }
}
