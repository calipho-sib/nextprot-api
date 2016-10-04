package org.nextprot.api.core.domain.publication;

/**
 * Publication types as defined in nextprot database
 *
 * Created by fnikitin on 03/02/16.
 */
public enum PublicationType {

    ARTICLE, PATENT, BOOK, THESIS, SUBMISSION, ONLINE_PUBLICATION, UNPUBLISHED_OBSERVATION, DOCUMENT;

    public static PublicationType valueOfName(String name) {

        switch(name) {

            case "ARTICLE":
                return ARTICLE;
            case "PATENT":
                return PATENT;
            case "BOOK":
                return BOOK;
            case "THESIS":
                return THESIS;
            case "SUBMISSION":
                return SUBMISSION;
            case "ONLINE_PUBLICATION":
                return ONLINE_PUBLICATION;
            case "UNPUBLISHED_OBSERVATION":
                return UNPUBLISHED_OBSERVATION;
            case "DOCUMENT":
                return DOCUMENT;
            default:
                throw new IllegalArgumentException("No enum constant PublicationType." + name);
        }
    }
}
