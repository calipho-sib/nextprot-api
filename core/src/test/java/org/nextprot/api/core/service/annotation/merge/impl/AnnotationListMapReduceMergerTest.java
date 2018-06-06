package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.service.annotation.merge.AnnotationListMergerBaseTest;

public class AnnotationListMapReduceMergerTest extends AnnotationListMergerBaseTest<AnnotationListMapReduceMerger> {

    @Override
    protected AnnotationListMapReduceMerger createMerger() {

        return new AnnotationListMapReduceMerger();
    }
}