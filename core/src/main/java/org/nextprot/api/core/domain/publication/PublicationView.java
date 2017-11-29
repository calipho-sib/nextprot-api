package org.nextprot.api.core.domain.publication;


import org.nextprot.api.commons.utils.EnumConstantDictionary;

import java.util.Map;

/**
 * Publication criteria as defined in nextprot database
 */
public enum PublicationView {

    CURATED, ADDITIONAL, PATENT, SUBMISSION, WEB_RESOURCE
    ;

    private static EnumConstantDictionary<PublicationView> dictionaryOfConstants = new EnumConstantDictionary<PublicationView>(PublicationView.class, values()) {
        @Override
        protected void updateDictionaryOfConstants(Map<String, PublicationView> dictionary) {
            dictionary.put("WEB-RESOURCE", WEB_RESOURCE);
        }
    };

    public static boolean hasName(String name) {

        return dictionaryOfConstants.haskey(name);
    }

    public static PublicationView valueOfName(String name) {

        return dictionaryOfConstants.valueOfKey(name);
    }
}
