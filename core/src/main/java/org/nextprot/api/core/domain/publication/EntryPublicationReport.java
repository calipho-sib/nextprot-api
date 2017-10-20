package org.nextprot.api.core.domain.publication;


import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class EntryPublicationReport implements Serializable {

    private static final long serialVersionUID = 0L;

    private String entryAccession;

    private Map<Long, EntryPublication> reportData;
    private List<EntryPublication> publications;
    private Map<PublicationView, List<EntryPublication>> publicationsByView;

    public void setReportData(Map<Long, EntryPublication> reportData) {

        this.reportData = reportData;
        publicationsByView = new HashMap<>();

        for (EntryPublication entryPublication : reportData.values()) {
            if (entryPublication.isCurated()) {
                publicationsByView.putIfAbsent(PublicationView.CURATED, new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isAdditional()) {
                publicationsByView.putIfAbsent(PublicationView.ADDITIONAL, new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isOnline()) {
                publicationsByView.putIfAbsent(PublicationView.WEB_RESOURCE, new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isSubmission()) {
                publicationsByView.putIfAbsent(PublicationView.SUBMISSION, new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isPatent()) {
                publicationsByView.putIfAbsent(PublicationView.PATENT, new ArrayList<>()).add(entryPublication);
            }
        }

        for (PublicationView view : publicationsByView.keySet()) {

            publicationsByView.get(view).sort(Comparator.comparingLong(EntryPublication::getId));
        }

        publications = reportData.values().stream()
                .sorted(Comparator.comparingLong(EntryPublication::getId))
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
