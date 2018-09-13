package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.annotation.merge.ObjectMatcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class FeaturePositionMatcher implements ObjectMatcher<Map<String, AnnotationIsoformSpecificity>> {

    private static final Logger LOGGER = Logger.getLogger(FeaturePositionMatcher.class.getName());

    @Override
    public boolean match(Map<String, AnnotationIsoformSpecificity> m1, Map<String, AnnotationIsoformSpecificity> m2) {

        Set<String> commonIsoMaps = Sets.intersection(m1.keySet(), m2.keySet());
        Set<String> sameIsoMaps = new HashSet<>();

        for (String isoformName : commonIsoMaps) {

            if (m1.get(isoformName).hasSameIsoformPositions(m2.get(isoformName))) {

                sameIsoMaps.add(isoformName);
            }
        }

        if (!sameIsoMaps.isEmpty()) {

            if (sameIsoMaps.size() == commonIsoMaps.size()) {
                return true;
            }

            Sets.difference(commonIsoMaps, sameIsoMaps).forEach(isoName -> {
                String message = "Conflicting positions in other isoform mapping " + isoName + ": first map=" +
                        m1.get(isoName).getFirstPosition() + "-" + m1.get(isoName).getLastPosition() + ", second map=" +
                        m2.get(isoName).getFirstPosition() + "-" + m2.get(isoName).getLastPosition();
                LOGGER.severe(message);
            });
        }

        return false;
    }
}