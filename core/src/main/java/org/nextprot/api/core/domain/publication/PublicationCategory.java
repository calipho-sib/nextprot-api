package org.nextprot.api.core.domain.publication;


import org.nextprot.commons.utils.EnumConstantDictionary;

import java.util.Map;

/**
 * Publication criteria as defined in nextprot database
 */
public enum PublicationCategory {

    CURATED, ADDITIONAL, PATENT, SUBMISSION, WEB_RESOURCE, ALL
    ;

    private static EnumConstantDictionary<PublicationCategory> dictionaryOfConstants = new EnumConstantDictionary<PublicationCategory>(PublicationCategory.class, values()) {
        @Override
        protected void updateDictionaryOfConstants(Map<String, PublicationCategory> dictionary) {
            dictionary.put("WEB-RESOURCE", WEB_RESOURCE);
        }
    };

    public static boolean hasName(String name) {

        return dictionaryOfConstants.haskey(name);
    }

    public static PublicationCategory valueOfName(String name) {

        return dictionaryOfConstants.valueOfKey(name);
    }
}
