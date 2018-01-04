package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;

import java.io.Serializable;

public class ThesisResourceLocator extends PublicationResourceLocator implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiObjectField(description = "The institute name")
    private String institute;

    @ApiObjectField(description = "The country")
    private String country;

    @Override
    public PublicationType getPublicationType() {
        return PublicationType.THESIS;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
