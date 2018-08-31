package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.EnrichableAnnotationListBaseTest;

import java.util.List;

public class EnrichedAnnotationListTest extends EnrichableAnnotationListBaseTest<EnrichedAnnotationList> {

    @Override
    protected EnrichedAnnotationList createEnrichableAnnotationList(List<Annotation> original) {

        return new EnrichedAnnotationList(original);
    }
}