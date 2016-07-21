package com.nextprot.api.isoform.mapper.utils;

import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for nextprot Isoform
 */
public class EntryIsoformUtils {

    /**
     * Get all isoforms except the given one
     */
    public static List<Isoform> getOtherIsoforms(Entry entry, String isoformUniqueName) {

        return entry.getIsoforms().stream()
                .filter(iso -> !iso.getUniqueName().equals(isoformUniqueName))
                .collect(Collectors.toList());
    }

    /**
     * Return an isoform object having unique name, main name or synonym equals to name
     * @param accession an isoform unique name (ac), main name or synonym
     * @return
     */
    public static Isoform getIsoformByName(Entry entry, String accession) {

        if (accession==null) return null;
        for (Isoform iso: entry.getIsoforms()) {
            if (accession.equals(iso.getUniqueName())) return iso;
            EntityName mainEname = iso.getMainEntityName();
            if (mainEname!=null && accession.equals(mainEname.getName())) return iso;
            for (EntityName syn: iso.getSynonyms()) {
                if (accession.equals(syn.getName())) return iso;
            }
        }
        return null;
    }

    public static Isoform getCanonicalIsoform(Entry entry) {

        for (Isoform iso : entry.getIsoforms()) {

            if (iso.isCanonicalIsoform())
                return iso;
        }

        return null;
    }
}
