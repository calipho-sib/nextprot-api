package org.nextprot.api.core.domain.publication;

import org.nextprot.api.commons.utils.EnumConstantDictionary;

import java.util.Map;

/**
 * Publication types as defined in nextprot database
 *
 * Created by fnikitin on 03/02/16.
 */
public enum PublicationType {

    ARTICLE, PATENT, BOOK, THESIS, SUBMISSION, ONLINE_PUBLICATION, UNPUBLISHED_OBSERVATION, DOCUMENT;

    private static EnumConstantDictionary<PublicationType> dictionaryOfConstants = new EnumConstantDictionary<PublicationType>(PublicationType.class, values()) {
        @Override
        protected void updateDictionaryOfConstants(Map<String, PublicationType> dictionary) {
            dictionary.put("UNPUBLISHED OBSERVATION", UNPUBLISHED_OBSERVATION);
            dictionary.put("ONLINE PUBLICATION", ONLINE_PUBLICATION);
        }
    };

    public static PublicationType valueOfName(String name) {

        return dictionaryOfConstants.valueOfKey(name);
    }
}
