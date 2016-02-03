package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.domain.PublicationAuthor;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class EditedVolumeBookLocator extends BookMediumLocator<BookLocation> implements Serializable {

    private static final long serialVersionUID = 0L;

    public EditedVolumeBookLocator(PublicationType publicationType) {
        super(publicationType);
    }

    @ApiObjectField(description = "The list of editors")
    private SortedSet<PublicationAuthor> editors = new TreeSet<>();

    public boolean hasEditors() {
        return !editors.isEmpty();
    }

    public SortedSet<PublicationAuthor> getEditors() {
        return editors;
    }

    public void addEditors(Set<PublicationAuthor> editors) {
        this.editors.addAll(editors);
    }

    @Override
    PublicationType getExpectedPublicationType() {
        return PublicationType.BOOK;
    }
}
