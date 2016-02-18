package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.domain.PublicationAuthor;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An edited volume book is a collection of chapters contributed by different authors and harmonized by an or many editor(s)
 */
public class EditedVolumeBookLocation extends BookLocation implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiObjectField(description = "The publisher name")
    private String publisher;

    @ApiObjectField(description = "The publisher city")
    private String city;

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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    PublicationType getPublicationType() {
        return PublicationType.BOOK;
    }
}
