package org.nextprot.api.core.domain.publication;

import org.nextprot.api.core.domain.Publication;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EntryPublicationView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Publication publication;
    private Map<String,String> citedInViews;
    private List<PublicationDirectLink> directLinks;

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Map<String, String> getCitedInViews() {
        return citedInViews;
    }

    public void setCitedInViews(Map<String, String> citedInViews) {
        this.citedInViews = citedInViews;
    }

    public List<PublicationDirectLink> getDirectLinks() {
        return directLinks;
    }

    public void setDirectLinks(List<PublicationDirectLink> directLinks) {
        this.directLinks = directLinks;
    }
}
