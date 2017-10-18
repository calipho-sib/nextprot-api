package org.nextprot.api.core.domain.publication;


import java.util.HashMap;
import java.util.Map;

/**
 * Publication criteria as defined in nextprot database
 */
public enum PublicationView {

    CURATED, ADDITIONAL, PATENT, SUBMISSION, WEB_RESOURCE
    ;

    private static Map<String, PublicationView> map = null;

    /**
     * @return a lazily built map of publication types
     */
    private static Map<String, PublicationView> getMap() {

        if (map == null) {

            Map<String, PublicationView> m = new HashMap<>(PublicationView.values().length);

            for (PublicationView constant : values()) {
                m.put(constant.name(), constant);
            }
            m.put("WEB-RESOURCE", WEB_RESOURCE);

            map = m;
        }

        return map;
    }

    public static boolean hasName(String name) {

        return getMap().containsKey(name);
    }

    public static PublicationView valueOfName(String name) {

        PublicationView result = getMap().get(name);

        if (result != null) {

            return result;
        }

        throw new IllegalArgumentException("No enum constant PublicationType." + name);
    }
}
