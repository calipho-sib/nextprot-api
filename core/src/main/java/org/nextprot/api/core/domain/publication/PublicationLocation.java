package org.nextprot.api.core.domain.publication;

import com.google.common.base.Preconditions;
import org.jsondoc.core.annotation.ApiObjectField;

/**
 * Defines informations relative to location of a publication
 *
 * Created by fnikitin on 03/02/16.
 */
public abstract class PublicationLocation {

    @ApiObjectField(description = "The publication container name")
    private String name;

    PublicationLocation(PublicationType publicationType) {

        Preconditions.checkNotNull(getPublicationType());
        Preconditions.checkNotNull(publicationType);

        Preconditions.checkState(getPublicationType() == publicationType);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract PublicationType getPublicationType();
}
