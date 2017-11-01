package org.nextprot.api.core.domain.publication;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;


/**
 * Collection of Publications informations associated with a neXtProt entry
 */
public class EntryPublications implements Serializable {

    private static final long serialVersionUID = 3L;

    private String entryAccession;
    private Map<Long, EntryPublication> entryPublicationsById;
    private Map<PublicationView, List<EntryPublication>> entryPublicationsByView;

    public String getEntryAccession() {
        return entryAccession;
    }

    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    public void setEntryPublications(Map<Long, EntryPublication> entryPublicationsById) {

        this.entryPublicationsById = entryPublicationsById;
        entryPublicationsByView = new HashMap<>();

        for (EntryPublication entryPublication : entryPublicationsById.values()) {
            if (entryPublication.isCurated()) {
                entryPublicationsByView.computeIfAbsent(PublicationView.CURATED, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isAdditional()) {
                entryPublicationsByView.computeIfAbsent(PublicationView.ADDITIONAL, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isOnline()) {
                entryPublicationsByView.computeIfAbsent(PublicationView.WEB_RESOURCE, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isSubmission()) {
                entryPublicationsByView.computeIfAbsent(PublicationView.SUBMISSION, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isPatent()) {
                entryPublicationsByView.computeIfAbsent(PublicationView.PATENT, k -> new ArrayList<>()).add(entryPublication);
            }
        }

        for (PublicationView view : entryPublicationsByView.keySet()) {

            entryPublicationsByView.get(view).sort(Comparator.comparingLong(EntryPublication::getPubId));
        }
    }

    @JsonIgnore
    public Map<Long, EntryPublication> getEntryPublicationsById() {
        return entryPublicationsById;
    }

    public EntryPublication getEntryPublication(long pubId) {
        return entryPublicationsById.get(pubId);
    }

    public Map<PublicationView, List<EntryPublication>> getEntryPublicationsByView() {

        return entryPublicationsByView;
    }

    public List<EntryPublication> getEntryPublicationList(PublicationView view) {

        return entryPublicationsByView.getOrDefault(view, new ArrayList<>());
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
