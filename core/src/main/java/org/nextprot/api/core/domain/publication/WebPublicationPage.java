package org.nextprot.api.core.domain.publication;

import java.io.Serializable;

/**
 * An online publication is accessible from the web
 */
public class WebPublicationPage extends PublicationLocation implements Serializable {

    private static final long serialVersionUID = 0L;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    PublicationType getPublicationType() {
        return PublicationType.ONLINE_PUBLICATION;
    }
}