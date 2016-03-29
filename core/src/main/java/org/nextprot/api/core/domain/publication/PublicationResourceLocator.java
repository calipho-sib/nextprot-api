package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;

/**
 * A Publication Resource Locator (or PRL) is a reference to a publication resource (a journal, a book or even a web page)
 *
 * Created by fnikitin on 03/02/16.
 */
public abstract class PublicationResourceLocator {

    @ApiObjectField(description = "The publication container name")
    private String name;

    public PublicationResourceLocator() { }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract PublicationType getPublicationType();
}
