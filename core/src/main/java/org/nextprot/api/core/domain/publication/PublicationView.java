package org.nextprot.api.core.domain.publication;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Publication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicationView implements Serializable {

    private static final long serialVersionUID = 3L;

    private Publication publication;
    private int relatedEntryCount;
    private Map<String, EntryPublication> entryPublicationMap = new HashMap<>();
    private Map<String, Map<String, Object>> entrySolrResultMap = new HashMap<>();

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Map<String, EntryPublication> getEntryPublicationMap() {
        return entryPublicationMap;
    }

    public Map<String, Map<String, Object>> getEntrySolrResultMap() {
        return entrySolrResultMap;
    }

    public void addEntryPublicationList(List<EntryPublication> entryPublicationList) {

        for (EntryPublication ep : entryPublicationList) {

            entryPublicationMap.put(ep.getEntryAccession(), ep);
        }
    }

    public void putEntrySolrResult(Map<String, Object> result) {

        String accession = (String)result.get("id");

        if (entrySolrResultMap.containsKey(accession)) {
            throw new NextProtException("accession "+accession+" already exists");
        }
        entrySolrResultMap.put(accession, result);
    }

    public int getRelatedEntryCount() {
        return relatedEntryCount;
    }

    public void setRelatedEntryCount(int relatedEntryCount) {
        this.relatedEntryCount = relatedEntryCount;
    }
}
