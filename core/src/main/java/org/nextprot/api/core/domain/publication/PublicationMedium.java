package org.nextprot.api.core.domain.publication;

import com.google.common.base.Preconditions;
import org.jsondoc.core.annotation.ApiObjectField;

/**
 * A medium that contains publications
 *
 * Created by fnikitin on 03/02/16.
 */
public abstract class PublicationMedium {

    @ApiObjectField(description = "The resource locator name")
    private String name;

    PublicationMedium(PublicationType publicationType) {

        Preconditions.checkNotNull(getExpectedPublicationType());
        Preconditions.checkNotNull(publicationType);

        Preconditions.checkState(getExpectedPublicationType() == publicationType);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract PublicationType getExpectedPublicationType();
}
