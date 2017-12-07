package org.nextprot.api.core.domain.publication;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Collection of Publications informations associated with a neXtProt entry
 */
public class EntryPublications implements Serializable {

    private static final long serialVersionUID = 4L;

    private String entryAccession;
    private Map<Long, EntryPublication> entryPublicationsById;
    private Map<PublicationCategory, List<EntryPublication>> entryPublicationsByCategory;

    public String getEntryAccession() {
        return entryAccession;
    }

    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    public void setData(Map<Long, EntryPublication> entryPublicationsById) {

        this.entryPublicationsById = entryPublicationsById;
        entryPublicationsByCategory = new HashMap<>();

        for (EntryPublication entryPublication : entryPublicationsById.values()) {
            if (entryPublication.isCurated()) {
                entryPublicationsByCategory.computeIfAbsent(PublicationCategory.CURATED, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isAdditional()) {
                entryPublicationsByCategory.computeIfAbsent(PublicationCategory.ADDITIONAL, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isOnline()) {
                entryPublicationsByCategory.computeIfAbsent(PublicationCategory.WEB_RESOURCE, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isSubmission()) {
                entryPublicationsByCategory.computeIfAbsent(PublicationCategory.SUBMISSION, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isPatent()) {
                entryPublicationsByCategory.computeIfAbsent(PublicationCategory.PATENT, k -> new ArrayList<>()).add(entryPublication);
            }
        }

        for (PublicationCategory view : entryPublicationsByCategory.keySet()) {

            entryPublicationsByCategory.get(view).sort(Comparator.comparingLong(EntryPublication::getPubId));
        }
    }

    @JsonIgnore
    public Map<Long, EntryPublication> getEntryPublicationsById() {
        return entryPublicationsById;
    }

    public EntryPublication getEntryPublication(long pubId) {
        return entryPublicationsById.get(pubId);
    }

    public Map<PublicationCategory, List<EntryPublication>> getEntryPublicationsByCategory() {

        return entryPublicationsByCategory;
    }

    public List<EntryPublication> getEntryPublicationList(PublicationCategory category) {

        if (category == PublicationCategory.ALL) {
            return entryPublicationsByCategory.values().stream()
                    .flatMap(l -> l.stream()).collect(Collectors.toList());
        }
        return entryPublicationsByCategory.getOrDefault(category, new ArrayList<>());
    }

    /* useful ?
    public List<EntryPublication> getEntryPublicationCitedList() {
        return orderedPubIdList.stream().map(id -> reportData.get(id)).filter(ep -> ep.isCited()).collect(Collectors.toList());
    }
    public List<EntryPublication> getEntryPublicationUncitedList() {
        return orderedPubIdList.stream().map(id -> reportData.get(id)).filter(ep -> ep.isUncited()).collect(Collectors.toList());
    }
    */
}
