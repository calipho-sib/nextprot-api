package org.nextprot.api.core.domain.publication;


import org.nextprot.api.commons.utils.EnumDictionary;

import java.util.Map;

/**
 * Publication criteria as defined in nextprot database
 */
public enum PublicationView {

    CURATED, ADDITIONAL, PATENT, SUBMISSION, WEB_RESOURCE
    ;

    private static EnumDictionary<PublicationView> dictionary = new EnumDictionary<PublicationView>(PublicationView.class, values()) {
        @Override
        protected void updateDictionary(Map<String, PublicationView> dictionary) {
            dictionary.put("WEB-RESOURCE", WEB_RESOURCE);
        }
    };

    public static boolean hasName(String name) {

        return dictionary.haskey(name);
    }

    public static PublicationView valueOfName(String name) {

        return dictionary.valueOfKey(name);
    }
}
