package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationMerger;
import org.nextprot.api.core.utils.annot.merge.AnnotationListMerger;

import java.util.List;

/**
 * Merge source annotations into destination annotations (merge destination annotations if needed)
 *
 * Created by fnikitin on 08/08/16.
 */
public class AnnotationListMergerImpl implements AnnotationListMerger {

    @Override
    public List<Annotation> merge(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

        // TODO: for performance reason merge should return the list of merged annotations without updating state of destAnnotationList
        AnnotationMerger updater = new AnnotationUpdater();

        for (Annotation srcAnnotation : srcAnnotationList) {

            AnnotationFinder finder = AnnotationFinder.valueOf(srcAnnotation.getAPICategory());

            Annotation foundAnnotation = finder.find(srcAnnotation, destAnnotationList);

            // not found -> add new annotation
            if (foundAnnotation == null) {

                destAnnotationList.add(srcAnnotation);
            }
            // found -> merge annotation with statementAnnotation
            else {

                updater.merge(foundAnnotation, srcAnnotation);
            }
        }

        return destAnnotationList;
    }
}
