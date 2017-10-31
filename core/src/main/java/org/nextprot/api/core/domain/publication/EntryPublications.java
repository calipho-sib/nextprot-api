package org.nextprot.api.core.domain.publication;


import java.io.Serializable;
import java.util.*;


/**
 * Collection of Publications informations associated with a neXtProt entry
 */
public class EntryPublications implements Serializable {

    private static final long serialVersionUID = 2L;

    private String entryAccession;
    private Map<Long, EntryPublication> reportData;
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

    public Map<PublicationView, List<EntryPublication>> getEntryPublicationsMap() {

        return publicationsByView;
    }

    public List<EntryPublication> getEntryPublicationList(PublicationView view) {

        return publicationsByView.getOrDefault(view, new ArrayList<>());
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
