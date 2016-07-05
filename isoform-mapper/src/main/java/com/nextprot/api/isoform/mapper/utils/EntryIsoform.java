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
 * A nextprot Entry isoform
 */
public class EntryIsoform {

    private final Entry entry;
    private final Isoform isoform;

    private EntryIsoform(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(isoform);

        this.entry = entry;
        this.isoform = isoform;
    }

    public static EntryIsoform parseEntryIsoform(String accession, EntryBuilderService entryBuilderService) {

        Preconditions.checkNotNull(accession, "missing accession name (either entry name or isoform name)");
        Preconditions.checkNotNull(entryBuilderService);

        String entryName = parseEntryName(accession);
        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

        if (!isIsoformAccession(accession)) {
            return new EntryIsoform(entry, getCanonicalIsoform(entry));
        }
        return new EntryIsoform(entry, getIsoformByName(entry, accession));
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
