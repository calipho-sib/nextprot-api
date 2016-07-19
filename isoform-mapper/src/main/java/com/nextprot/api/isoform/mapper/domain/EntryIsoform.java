package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A nextprot Entry isoform
 */
public class EntryIsoform {

    private final String accession;
    private final Entry entry;
    private final Isoform isoform;

    public EntryIsoform(String accession, Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(accession);
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(isoform);

        this.accession = accession;
        this.entry = entry;
        this.isoform = isoform;
    }

    public String getAccession() {
        return accession;
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

    public Isoform getIsoformByName(String name) {

        return EntryIsoform.getIsoformByName(entry, name);
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

    public static int getIsoformNumber(Isoform isoform) {

        String accession = isoform.getUniqueName();

        if (accession.contains("-")) {

            return Integer.valueOf(accession.substring(accession.lastIndexOf("-")+1));
        }

        return -1;
    }
}
