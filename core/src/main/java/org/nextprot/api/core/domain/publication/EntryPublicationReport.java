package org.nextprot.api.core.domain.publication;


import java.io.Serializable;
import java.util.*;


public class EntryPublicationReport implements Serializable {

    private static final long serialVersionUID = 0L;

    private String entryAccession;

    private Map<Long, EntryPublication> reportData;
    private Map<PublicationView, List<EntryPublication>> reportByView;

    public void setReportData(Map<Long, EntryPublication> reportData) {

        this.reportData = reportData;
        reportByView = new HashMap<>();

        for (EntryPublication entryPublication : reportData.values()) {
            if (entryPublication.isCurated()) {
                reportByView.computeIfAbsent(PublicationView.CURATED, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isAdditional()) {
                reportByView.computeIfAbsent(PublicationView.ADDITIONAL, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isOnline()) {
                reportByView.computeIfAbsent(PublicationView.WEB_RESOURCE, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isSubmission()) {
                reportByView.computeIfAbsent(PublicationView.SUBMISSION, k -> new ArrayList<>()).add(entryPublication);
            }
            if (entryPublication.isPatent()) {
                reportByView.computeIfAbsent(PublicationView.PATENT, k -> new ArrayList<>()).add(entryPublication);
            }
        }

        for (PublicationView view : reportByView.keySet()) {

            reportByView.get(view).sort(Comparator.comparingLong(EntryPublication::getId));
        }
    }

    public Map<PublicationView, List<EntryPublication>> getReportDataView() {

        return reportByView;
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

        return reportByView.get(view);
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
