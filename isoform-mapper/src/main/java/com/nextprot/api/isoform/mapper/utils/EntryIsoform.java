package com.nextprot.api.isoform.mapper.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A nextprot isoform
 */
public class EntryIsoform {

    private final Entry entry;
    private final Isoform isoform;

    private EntryIsoform(Entry entry, Isoform isoform) {

        this.entry = entry;
        this.isoform = isoform;
    }

    public static EntryIsoform parseAccession(String accession, EntryBuilderService entryBuilderService) {

        Preconditions.checkNotNull(accession, "missing accession name (either entry name or isoform name)");
        Preconditions.checkNotNull(entryBuilderService);

        String entryAccession;
        String isoformAccession = null;

        // isoform accession
        if (accession.contains("-")) {
            int colonPosition = accession.indexOf("-");
            entryAccession = accession.substring(0, colonPosition);
            isoformAccession = accession;
        }
        // entry accession
        else {
            entryAccession = accession;
        }

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything());
        Isoform isoform;
        Propagator propagator = new Propagator(entry);

        if (isoformAccession == null) {
            isoform = propagator.getCanonicalIsoform();
        } else {
            isoform = propagator.getIsoformByName(isoformAccession);
        }

        return new EntryIsoform(entry, isoform);
    }

    public Entry getEntry() {
        return entry;
    }

    public Isoform getIsoform() {
        return isoform;
    }

    public boolean isCanonicalIsoform() {
        return isoform.isCanonicalIsoform();
    }

    /**
     * Get all isoforms except the given one
     */
    public List<Isoform> getOtherIsoforms() {

        return entry.getIsoforms().stream()
                .filter(iso -> !iso.getUniqueName().equals(isoform.getUniqueName()))
                .collect(Collectors.toList());
    }
}
