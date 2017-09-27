package org.nextprot.api.core.utils.annot.comp;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.utils.NullableComparable;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.*;


/**
 * Comparison function that sort Annotations based on feature positions.
 *
 * <h3>A. Selecting isoform feature positions</h3>
 * As Annotation can contain multiple isoform targets, we first have to choose one of them
 * with the following criteria:
 *
 * <pre>
 * if has a single isoform:
 *      pick this one
 * else
 *      select the one with the lowest feature position (see Section B)
 * endif
 * </pre>
 *
 * The feature positions of the selected isoforms are then compared as below
 *
 * <h3>B. Feature position based Annotation comparison</h3>
 *
 * <ol>
 *  <li>feature begin ASC</li>
 *  <li>feature end DESC</li>
 * </ol>
 */
class ByFeaturePositionComparator implements Comparator<Annotation> {

    private static final NullableComparable<Integer> NULLABLE_COMPARABLE = new NullableComparable<>();

    @Override
    public int compare(Annotation a1, Annotation a2) {

        String isoformName1 = selectIsoformNameForComparison(a1);
        String isoformName2 = selectIsoformNameForComparison(a2);

        return compareAnnotByNullablePosition(
                a1.getStartPositionForIsoform(isoformName1), a1.getEndPositionForIsoform(isoformName1),
                a2.getStartPositionForIsoform(isoformName2), a2.getEndPositionForIsoform(isoformName2)
        );
    }

    private String selectIsoformNameForComparison(Annotation annotation) {

        Map<String, AnnotationIsoformSpecificity> targets = annotation.getTargetingIsoformsMap();

        if (targets.size() == 1)
            return targets.keySet().iterator().next();
        else
            return getFirstIsoformSpecificity(targets.values()).getIsoformAccession();
    }

    static AnnotationIsoformSpecificity getFirstIsoformSpecificity(Collection<AnnotationIsoformSpecificity> targets) {

        Preconditions.checkNotNull(targets);
        Preconditions.checkArgument(!targets.isEmpty());

        Iterator<AnnotationIsoformSpecificity> iter = targets.iterator();

        AnnotationIsoformSpecificity first = iter.next();

        while (iter.hasNext()) {

            AnnotationIsoformSpecificity specificity = iter.next();

            int cmp = compareAnnotByNullablePosition(
                    specificity.getFirstPosition(), specificity.getLastPosition(),
                    first.getFirstPosition(), first.getLastPosition()
            );

            if (cmp < 0) {

                first = specificity;
            }
        }

        return first;
    }

    /*
        <h2>Comparison contract</h2>

        <h3>Unknown(null) begin comes first</h3>

        [1]   ?-------
        [2]   i---------

        <h3>Same begins greater ending comes first</h3>

        [1]   ----------
        [2]   ------
    */
    static int compareAnnotByNullablePosition(Integer begin1, Integer end1, Integer begin2, Integer end2) {

        int cmp;

        // 1. begin positions in ascending order
        // UNKNOWN BEGIN COMES FIRST
        cmp = NULLABLE_COMPARABLE.compareNullables(begin1, begin2, true);

        // 2. end positions in descending order (most inclusive comes first)
        if (cmp == 0) {

            // UNKNOWN END COMES LAST
            cmp = NULLABLE_COMPARABLE.compareNullables(end1, end2, false);
        }

        return cmp;
    }
}
