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
 *      select the one with the lowest feature position (see Section B)
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
 * </ol>
 * Created by fnikitin on 09/11/15.
 */
@Deprecated // use ByFeaturePositionComparator instead
class ByIsoformPositionComparator implements Comparator<Annotation> {

    private final String canonicalIsoformUniqueName;

    ByIsoformPositionComparator(Isoform canonicalIsoform) {

        Preconditions.checkNotNull(canonicalIsoform);
        Preconditions.checkArgument(canonicalIsoform.isCanonicalIsoform());

        this.canonicalIsoformUniqueName = canonicalIsoform.getIsoformAccession();
    }

    @Override
    public int compare(Annotation a1, Annotation a2) {

        String isoformName1 = selectIsoformNameForComparison(a1);
        String isoformName2 = selectIsoformNameForComparison(a2);

        int cmp = compareIsoCanonicalFirstThenByIsoName(isoformName1, isoformName2);

        if (cmp != 0) return cmp;

        return ByFeaturePositionComparator.compareAnnotByNullablePosition(
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
            return ByFeaturePositionComparator.getFirstIsoformSpecificity(targets.values()).getIsoformAccession();
    }

    private int compareIsoCanonicalFirstThenByIsoName(String isoformName1, String isoformName2) {

        boolean isIso1Canonical = isoformName1.equals(canonicalIsoformUniqueName);
        boolean isIso2Canonical = isoformName2.equals(canonicalIsoformUniqueName);

        if (isIso1Canonical && !isIso2Canonical) {
            return -1;
        } else if (!isIso1Canonical && isIso2Canonical) {
            return 1;
        }
        return isoformName1.compareTo(isoformName2);
    }
}
