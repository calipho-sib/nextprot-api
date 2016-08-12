package org.nextprot.api.core.utils.annot.comp;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.*;

/**
 * Comparison function that sort Annotations based on feature positions of selected isoforms.
 *
 * <h3>A. Selecting isoform feature positions</h3>
 * As Annotation can contain multiple isoform targets, we first have to choose one of them
 * with the following criteria:
 *
 * <pre>
 * if has a single isoform:
 *      pick this one
 * else if has canonical
 *      pick the canonical one
 * else
 *      select the one with the minimum feature position (see B)
 * endif
 * </pre>
 *
 * The feature positions of the selected isoforms are then compared as below
 *
 * <h3>B. Feature position based Annotation comparison</h3>
 *
 * <ol>
 *  <li>canonical isoform target feature comes first</li>
 *  <li>feature begin ASC</li>
 *  <li>feature end DESC</li>
 *  <li>feature annotation_id ASC</li>
 * </ol>
 * Created by fnikitin on 09/11/15.
 */
public class ByIsoformPositionComparator implements Comparator<Annotation> {

    private final String canonicalIsoformUniqueName;

    public ByIsoformPositionComparator(Isoform canonicalIsoform) {

        Preconditions.checkNotNull(canonicalIsoform);
        Preconditions.checkArgument(canonicalIsoform.isCanonicalIsoform());

        this.canonicalIsoformUniqueName = canonicalIsoform.getIsoformAccession();
    }

    @Override
    public int compare(Annotation a1, Annotation a2) {

        String isoformName1 = selectIsoformNameForComparison(a1);
        String isoformName2 = selectIsoformNameForComparison(a2);

        int cmp = compareIsoformCanonicalFirst(isoformName1, isoformName2);

        if (cmp != 0) return cmp;

        return compareAnnotByPosition(
                a1.getStartPositionForIsoform(isoformName1), a1.getEndPositionForIsoform(isoformName1),
                a2.getStartPositionForIsoform(isoformName2), a2.getEndPositionForIsoform(isoformName2)
        );
    }

    private String selectIsoformNameForComparison(Annotation annotation) {

        Map<String, AnnotationIsoformSpecificity> targets = annotation.getTargetingIsoformsMap();

        if (targets.size() == 1)
            return targets.keySet().iterator().next();
        else if (targets.containsKey(canonicalIsoformUniqueName))
            return canonicalIsoformUniqueName;
        else
            return getFirstIsoformSpecificity(targets.values()).getIsoformName();
    }

    AnnotationIsoformSpecificity getFirstIsoformSpecificity(Collection<AnnotationIsoformSpecificity> targets) {

        Preconditions.checkNotNull(targets);
        Preconditions.checkArgument(!targets.isEmpty());

        Iterator<AnnotationIsoformSpecificity> iter = targets.iterator();

        AnnotationIsoformSpecificity first = iter.next();

        while (iter.hasNext()) {

            AnnotationIsoformSpecificity specificity = iter.next();

            int cmp = compareAnnotByPosition(
                    specificity.getFirstPosition(), specificity.getLastPosition(),
                    first.getFirstPosition(), first.getLastPosition()
            );

            if (cmp < 0) {

                first = specificity;
            }
        }

        return first;
    }

    private int compareIsoformCanonicalFirst(String isoformName1, String isoformName2) {

        boolean isIso1Canonical = isoformName1.equals(canonicalIsoformUniqueName);
        boolean isIso2Canonical = isoformName2.equals(canonicalIsoformUniqueName);

        if (isIso1Canonical && !isIso2Canonical) {
            return -1;
        }
        else if (!isIso1Canonical && isIso2Canonical) {
            return 1;
        }

        return 0;
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
    int compareAnnotByPosition(Integer begin1, Integer end1, Integer begin2, Integer end2) {

        int cmp;

        // 1. begin positions in ascending order
        // UNKNOWN BEGIN COMES FIRST
        cmp = compareNullablePositions(begin1, begin2, true);

        // 2. end positions in descending order (most inclusive comes first)
        if (cmp == 0) {

            // UNKNOWN END COMES LAST
            cmp = compareNullablePositions(end1, end2, false);
        }

        return cmp;
    }

    int compareNullablePositions(Integer i1, Integer i2, boolean asc) {

        int cmp;

        if (Objects.equals(i1, i2)) return 0;

        if (i1 == null)
            cmp = -1;
        else if (i2 == null)
            cmp = 1;
        else
            cmp = i1.compareTo(i2);

        return (asc) ? cmp : -cmp;
    }
}
