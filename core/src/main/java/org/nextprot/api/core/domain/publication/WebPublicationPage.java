package org.nextprot.api.core.domain.publication;

import java.io.Serializable;

public class WebPublicationPage extends PublicationMedium implements Serializable {

    private static final long serialVersionUID = 0L;

    private String url;

    public WebPublicationPage(PublicationType publicationType) {
        super(publicationType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    PublicationType getExpectedPublicationType() {
        return PublicationType.ONLINE_PUBLICATION;
    }
}