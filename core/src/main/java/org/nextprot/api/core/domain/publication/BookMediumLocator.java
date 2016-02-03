package org.nextprot.api.core.domain.publication;

/**
 * A book medium that stores publication with a location
 *
 * Created by fnikitin on 03/02/16.
 */
public abstract class BookMediumLocator<T extends BookLocation> extends PublicationMedium {

    private T bookLocation;

    BookMediumLocator(PublicationType publicationType) {
        super(publicationType);
    }

    public T getLocation() {
        return bookLocation;
    }

    public void setLocation(T bookLocation) {
        this.bookLocation = bookLocation;
    }
}
