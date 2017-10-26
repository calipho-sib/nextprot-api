package org.nextprot.api.core.domain.publication;


import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Collection of Publications associated with a neXtProt entry
 */
public class EntryPublications implements Serializable {

    private static final long serialVersionUID = 1L;

    private String entryAccession;
    private Map<Long, EntryPublication> reportData;
    private List<EntryPublication> publications;
    private Map<PublicationView, List<EntryPublication>> publicationsByView;

    public void setReportData(Map<Long, EntryPublication> reportData) {

        this.reportData = reportData;
        publicationsByView = new HashMap<>();

        for (EntryPublication entryPublication : reportData.values()) {
            if (entryPublication.isCurated()) {
                publicationsByView.computeIfAbsent(PublicationView.CURATED, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isAdditional()) {
                publicationsByView.computeIfAbsent(PublicationView.ADDITIONAL, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isOnline()) {
                publicationsByView.computeIfAbsent(PublicationView.WEB_RESOURCE, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isSubmission()) {
                publicationsByView.computeIfAbsent(PublicationView.SUBMISSION, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isPatent()) {
                publicationsByView.computeIfAbsent(PublicationView.PATENT, k -> new ArrayList<>()).add(entryPublication);
            }
        }

        for (PublicationView view : publicationsByView.keySet()) {

            publicationsByView.get(view).sort(Comparator.comparingLong(EntryPublication::getPubId));
        }

        publications = reportData.values().stream()
                .sorted(Comparator.comparingLong(EntryPublication::getPubId))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<EntryPublication> getPublications() {

        return publications;
    }

    public Map<PublicationView, List<EntryPublication>> getPublicationsByView() {

        return publicationsByView;
    }

    public EntryPublication getEntryPublication(long pubId) {
        return reportData.get(pubId);
    }

    public String getEntryAccession() {
        return entryAccession;
    }

    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    public List<EntryPublication> getEntryPublicationList(PublicationView view) {

        return publicationsByView.get(view);
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
