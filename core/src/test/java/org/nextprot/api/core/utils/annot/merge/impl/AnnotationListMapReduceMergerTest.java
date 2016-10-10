package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.core.utils.annot.merge.AnnotationListMergerBaseTest;

public class AnnotationListMapReduceMergerTest extends AnnotationListMergerBaseTest<AnnotationListMapReduceMerger> {

    @Override
    protected AnnotationListMapReduceMerger createMerger() {

        return new AnnotationListMapReduceMerger();
    }
}