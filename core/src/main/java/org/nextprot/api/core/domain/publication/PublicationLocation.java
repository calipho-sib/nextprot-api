package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;

/**
 * Defines informations relative to location of a publication
 *
 * Created by fnikitin on 03/02/16.
 */
public abstract class PublicationLocation {

    @ApiObjectField(description = "The publication container name")
    private String name;

    public PublicationLocation() { }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract PublicationType getPublicationType();
}
