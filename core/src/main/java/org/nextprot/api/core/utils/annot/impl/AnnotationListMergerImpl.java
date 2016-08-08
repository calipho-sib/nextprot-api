package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUpdater;
import org.nextprot.api.core.utils.annot.AnnotationListMerger;

import java.util.List;

/**
 * Merge source annotations into destination annotations (update destination annotations if needed)
 *
 * Created by fnikitin on 08/08/16.
 */
public class AnnotationListMergerImpl implements AnnotationListMerger {

    @Override
    public List<Annotation> merge(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

        // TODO: for performance reason merge should return the list of merged annotations without updating state of destAnnotationList
        AnnotationUpdater updater = new AnnotationUpdaterImpl();

        for (Annotation srcAnnotation : srcAnnotationList) {

            AnnotationFinder finder = AnnotationFinder.valueOf(srcAnnotation.getAPICategory());

            Annotation foundAnnotation = finder.find(srcAnnotation, destAnnotationList);

            // not found -> add new annotation
            if (foundAnnotation == null) {

                destAnnotationList.add(srcAnnotation);
            }
            // found -> update annotation with statementAnnotation
            else {

                updater.update(foundAnnotation, srcAnnotation);
            }
        }

        return destAnnotationList;
    }
}
