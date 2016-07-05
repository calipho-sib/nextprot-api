package com.nextprot.api.isoform.mapper.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.dao.EntityName;
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

    public static EntryIsoform parseEntryIsoform(String accession, EntryBuilderService entryBuilderService) {

        Preconditions.checkNotNull(accession, "missing accession name (either entry name or isoform name)");
        Preconditions.checkNotNull(entryBuilderService);

        String entryAccession;
        String isoformAccession = null;

        // isoform accession
        if (accession.contains("-")) {
            int dashPosition = accession.indexOf("-");
            entryAccession = accession.substring(0, dashPosition);
            isoformAccession = accession;
        }
        // entry accession
        else {
            entryAccession = accession;
        }

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything());
        Isoform isoform;

        if (isoformAccession == null) {
            isoform = getCanonicalIsoform(entry);
        } else {
            isoform = getIsoformByName(entry, isoformAccession);
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

    /**
     * Return an isoform object having unique name, main name or synonym equals to name
     * @param name an isoform unique name (ac), main name or synonym
     * @return
     */
    public static Isoform getIsoformByName(Entry entry, String name) {

        if (name==null) return null;
        for (Isoform iso: entry.getIsoforms()) {
            if (name.equals(iso.getUniqueName())) return iso;
            EntityName mainEname = iso.getMainEntityName();
            if (mainEname!=null && name.equals(mainEname.getName())) return iso;
            for (EntityName syn: iso.getSynonyms()) {
                if (name.equals(syn.getName())) return iso;
            }
        }
        return null;
    }

    /**
     * Return the canonical isoform of the given entry
     */
    public static Isoform getCanonicalIsoform(Entry entry) {

        for (Isoform iso: entry.getIsoforms()) {
            if (iso.isCanonicalIsoform()) return iso;
        }
        return null;
    }
}
