package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.core.utils.annot.merge.AnnotationListMergerBaseTest;


public class AnnotationListMergerImplTest extends AnnotationListMergerBaseTest<AnnotationListMergerImpl> {

    @Override
    protected AnnotationListMergerImpl createMerger() {

        return new AnnotationListMergerImpl();
    }
}