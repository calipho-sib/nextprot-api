package org.nextprot.api.core.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.*;

/**
 * Compare Annotation based on the following criteria:
 *
 * ....
 *
 * Created by fnikitin on 09/11/15.
 */
public class AnnotationComparator implements Comparator<Annotation> {

    private final String canonicalIsoformUniqueName;

    public AnnotationComparator(Isoform canonicalIsoform) {

        this.canonicalIsoformUniqueName = canonicalIsoform.getUniqueName();
    }

    @Override
    public int compare(Annotation a1, Annotation a2) {

        Preconditions.checkArgument(a1.getAPICategory() == a2.getAPICategory());

        String isoformName1 = selectIsoform(a1);
        String isoformName2 = selectIsoform(a2);

        return compare(a1.getStartPositionForIsoform(isoformName1), a1.getEndPositionForIsoform(isoformName1), a1.getAnnotationId(),
                a2.getStartPositionForIsoform(isoformName2), a2.getEndPositionForIsoform(isoformName2), a2.getAnnotationId());
    }

    private String selectIsoform(Annotation annotation) {

        Map<String, AnnotationIsoformSpecificity> targets = annotation.getTargetingIsoformsMap();

        if (targets.size() == 1) {
            return targets.keySet().iterator().next();
        } else {
            if (targets.containsKey(canonicalIsoformUniqueName)) {
                return canonicalIsoformUniqueName;
            } else {
                AnnotationIsoformSpecificity specificity = getMinimumSpecificity(targets.values());
                return specificity.getIsoformName();
            }
        }
    }

    /**
     * @return the isoform name with the minimum feature position
     */
    AnnotationIsoformSpecificity getMinimumSpecificity(Collection<AnnotationIsoformSpecificity> targets) {

        Preconditions.checkNotNull(targets);
        Preconditions.checkArgument(!targets.isEmpty());

        Iterator<AnnotationIsoformSpecificity> iter = targets.iterator();

        AnnotationIsoformSpecificity minSpecificity = iter.next();

        while (iter.hasNext()) {

            AnnotationIsoformSpecificity specificity = iter.next();

            int cmp = compare(specificity.getFirstPosition(), specificity.getLastPosition(), specificity.getAnnotationId(),
                    minSpecificity.getFirstPosition(), minSpecificity.getLastPosition(), minSpecificity.getAnnotationId());

            if (cmp < 0) {

                minSpecificity = specificity;
            }
        }

        return minSpecificity;
    }

    int compare(Integer begin1, Integer end1, long id1, Integer begin2, Integer end2, long id2) {

        int cmp;

        // 1. begin positions in ascending order
        // UNKNOWN BEGIN COMES FIRST
        cmp = compare(begin1, begin2, true);

        // 2. end positions in descending order (most inclusive comes first)
        if (cmp == 0) {

            // UNKNOWN END COMES LAST
            cmp = compare(end1, end2, false);
        }

        // 3. annotation id in ascending order
        if (cmp == 0) {

            cmp = (int) (id1 - id2);
        }

        return cmp;
    }

    int compare(Integer i1, Integer i2, boolean asc) {

        int cmp;

        if (Objects.equals(i1, i2)) {
            return 0;
        }

        if (i1 == null) {
            cmp = -1;
        }
        else if (i2 == null) {
            cmp = 1;
        }
        else {
            cmp = i1.compareTo(i2);
        }

        return (asc) ? cmp : -cmp;
    }
}
