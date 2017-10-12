package org.nextprot.api.core.domain.publication;

import java.util.HashMap;
import java.util.Map;

/**
 * Publication types as defined in nextprot database
 *
 * Created by fnikitin on 03/02/16.
 */
public enum PublicationType {

    ARTICLE, PATENT, BOOK, THESIS, SUBMISSION, ONLINE_PUBLICATION, UNPUBLISHED_OBSERVATION, DOCUMENT;

    private static Map<String, PublicationType> publicationTypeMap = null;

    /**
     * @return a lazily built map of publication types
     */
    private static Map<String, PublicationType> getPublicationTypeMap() {

        if (publicationTypeMap == null) {

            Map<String, PublicationType> m = new HashMap<>(PublicationType.values().length);
            for (PublicationType constant : values()) {
                m.put(constant.name(), constant);
            }
            m.put("UNPUBLISHED OBSERVATION", UNPUBLISHED_OBSERVATION);
            m.put("ONLINE PUBLICATION", ONLINE_PUBLICATION);

            publicationTypeMap = m;
        }
        return publicationTypeMap;
    }

    public static PublicationType valueOfName(String name) {

        PublicationType result = getPublicationTypeMap().get(name);

        if (result != null) {

            return result;
        }

        throw new IllegalArgumentException("No enum constant PublicationType." + name);
    }
}
