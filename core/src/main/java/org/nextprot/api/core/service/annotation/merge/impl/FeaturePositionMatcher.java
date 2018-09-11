package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.annotation.merge.ObjectMatcher;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class FeaturePositionMatcher implements ObjectMatcher<Map<String, AnnotationIsoformSpecificity>> {

    private static final Logger LOGGER = Logger.getLogger(FeaturePositionMatcher.class.getName());

    @Override
    public boolean match(Map<String, AnnotationIsoformSpecificity> m1, Map<String, AnnotationIsoformSpecificity> m2) {

        Set<String> isoNames = (m1.size() > m2.size()) ? m1.keySet() : m2.keySet();

        for (String name : isoNames) {

            if (m1.containsKey(name) && m2.containsKey(name) && m1.get(name).hasSameIsoformPositions(m2.get(name))) {

                isoNames.stream()
                        .filter(isoName -> !isoName.equals(name))
                        .filter(isoName -> m1.containsKey(isoName) && m2.containsKey(isoName))
                        .forEach(isoName -> {
                            // throw exception if other isoforms do not have same variant at same locations
                            if (!m1.get(isoName).hasSameIsoformPositions(m2.get(isoName))) {

                                String message = "conflicting positions for " + isoName + ": first map: " +
                                        m1.get(isoName).getFirstPosition() + "-" + m1.get(isoName).getLastPosition() + " vs second map: " +
                                        m2.get(isoName).getFirstPosition() + "-" + m2.get(isoName).getLastPosition();
                                LOGGER.warning(message);
                                throw new NextProtException(message);
                        }});

                return true;
            }
        }

        return false;
    }
}
