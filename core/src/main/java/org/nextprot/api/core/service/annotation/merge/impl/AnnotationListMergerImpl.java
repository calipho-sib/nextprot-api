package org.nextprot.api.core.service.annotation.merge.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationListMerger;
import org.nextprot.api.core.service.annotation.merge.AnnotationMerger;

import java.util.List;
import java.util.Optional;

/**
 * Merge source annotations into destination annotations (merge destination annotations if needed)
 *
 * Created by fnikitin on 08/08/16.
 */
public class AnnotationListMergerImpl implements AnnotationListMerger {

    protected static final Logger LOGGER = Logger.getLogger(AnnotationListMergerImpl.class);

    private final AnnotationFinder annotationFinder = new AnnotationFinder();

    @Override
    public List<Annotation> merge(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

        // TODO: for performance reason merge should return the list of merged annotations without updating state of destAnnotationList
        AnnotationMerger updater = new AnnotationUpdater();

        for (Annotation srcAnnotation : srcAnnotationList) {

            Optional<Annotation> foundAnnotation = annotationFinder.findAnnotationContainer(srcAnnotation, destAnnotationList);

            // not found -> add new annotation
            if (!foundAnnotation.isPresent()) {
                destAnnotationList.add(srcAnnotation);
            }
            // found -> merge annotation with statementAnnotation
            else {
                updater.merge(foundAnnotation.get(), srcAnnotation);
            }
        }

        return destAnnotationList;
    }
}
