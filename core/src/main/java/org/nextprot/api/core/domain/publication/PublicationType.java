package org.nextprot.api.core.domain.publication;

import org.nextprot.commons.utils.EnumConstantDictionary;

import java.util.Map;

/**
 * Publication types as defined in nextprot database
 *
 * <h3>Table in neXtProt</h3>
 * cv_id    cv_name                 cv_description
 * 10	    ARTICLE	                Publication of type article
 * 20	    PATENT	                Publication of type patent
 * 30	    BOOK	                Publication of type book
 * 40	    THESIS	                Publication of type thesis
 * 50	    SUBMISSION              Publication of type submission
 * 60	    ONLINE PUBLICATION	    Publication of type online publication
 * 70	    UNPUBLISHED OBSERVATION	Publication of type unpublished observations
 * 80	    DOCUMENT                External database entry document
 *
 * Created by fnikitin on 03/02/16.
 */
public enum PublicationType {

    ARTICLE, PATENT, BOOK, THESIS, SUBMISSION, ONLINE_PUBLICATION, UNPUBLISHED_OBSERVATION, DOCUMENT
    ;

    private final int id;
    PublicationType() {

        id = (ordinal()+1) * 10;
    }

    private static EnumConstantDictionary<PublicationType> dictionaryOfConstants = new EnumConstantDictionary<PublicationType>(PublicationType.class, values()) {
        @Override
        protected void updateDictionaryOfConstants(Map<String, PublicationType> dictionary) {
            dictionary.put("UNPUBLISHED OBSERVATION", UNPUBLISHED_OBSERVATION);
            dictionary.put("ONLINE PUBLICATION", ONLINE_PUBLICATION);
        }
    };

    public int getId() {
        return id;
    }

    public static PublicationType valueOfName(String name) {

        return dictionaryOfConstants.valueOfKey(name);
    }

    public static PublicationType valueOfId(int id) {

        int mod = id % 10;

        if (mod == 0 ) {
            int ordinal = (id /10) - 1;

            if (ordinal < values().length) {
                return values()[ordinal];
            }
        }

        throw new IllegalArgumentException("id "+id + " is not associated with a PublicationType");
    }
}
