package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.service.annotation.merge.AnnotationListMergerBaseTest;


public class AnnotationListMergerImplTest extends AnnotationListMergerBaseTest<AnnotationListMergerImpl> {

    @Override
    protected AnnotationListMergerImpl createMerger() {

        return new AnnotationListMergerImpl();
    }
}