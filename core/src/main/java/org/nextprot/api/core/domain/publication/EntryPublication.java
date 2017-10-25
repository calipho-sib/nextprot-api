package org.nextprot.api.core.domain.publication;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class EntryPublication implements Serializable {

    private static final long serialVersionUID = 1L;

    private String entryAccession;
    private long pubId;
    private boolean cited, uncited, patent, submission, online, curated, additional;
    private Map<String,String> citedInViews = new TreeMap<>();

    public EntryPublication(String entryAccession, long pubId) {
        this.entryAccession = entryAccession;
        this.pubId = pubId;
    }

    public String getEntryAccession() {
        return entryAccession;
    }

    public long getPubId() {
        return pubId;
    }
    public boolean isCited() {
        return cited;
    }
    public void setCited(boolean cited) {
        this.cited = cited;
    }
    public boolean isCurated() {
        return curated;
    }
    public void setCurated(boolean curated) {
        this.curated = curated;
    }
    public boolean isUncited() {
        return uncited;
    }
    public void setUncited(boolean uncited) {
        this.uncited = uncited;
    }
    public boolean isAdditional() {
        return additional;
    }
    public void setAdditional(boolean additional) {
        this.additional = additional;
    }
    public boolean isPatent() {
        return patent;
    }
    public void setPatent(boolean patent) {
        this.patent = patent;
    }
    public boolean isSubmission() {
        return submission;
    }
    public void setSubmission(boolean submission) {
        this.submission = submission;
    }
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }
    /**
     * A map describing the list of entry views in which we find the publication as the reference of an annotation evidence
     * The keys are sorted alphabetically
     * @return a map with where the key is the label of the page view, and the value the path of the corresponding nextprot page URL
     */
    public Map<String,String> getCitedInViews() {
        return  citedInViews;
    }
    public void addCitedInViews(String label, String link) {
        this.citedInViews.put(label,link);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id:").append(pubId).append(" ");
        sb.append("cited:").append(cited).append(" ");
        sb.append("uncited:").append(uncited).append(" ");
        sb.append("patent:").append(patent).append(" ");
        sb.append("submission:").append(submission).append(" ");
        sb.append("online:").append(online).append(" ");
        sb.append("curated:").append(curated).append(" ");
        sb.append("additional:").append(additional).append(" ");
        sb.append("in views:");
        for (Map.Entry<String,String>  v : citedInViews.entrySet()) sb.append(v.getKey()+"["+ v.getValue() +"]").append(", ");
        sb.append(" ");
        return sb.toString();
    }
}
