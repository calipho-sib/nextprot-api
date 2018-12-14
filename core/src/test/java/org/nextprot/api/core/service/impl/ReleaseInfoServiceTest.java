package org.nextprot.api.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.MasterIdentifierService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Valentine Rech de Laval
 * @since 2018-12-07
 */
public class ReleaseInfoServiceTest {

    @Mock
    private MasterIdentifierService masterIdentifierService = new MasterIdentifierServiceImpl();

    @Mock
    private AnnotationServiceImpl annotationService = new AnnotationServiceImpl();

    @InjectMocks
    private ReleaseInfoServiceImpl releaseInfoService = new ReleaseInfoServiceImpl();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(masterIdentifierService.findUniqueNames()).thenReturn(new HashSet<>(Arrays.asList(
                "WO_ANNOT", "W_NOT_FUNCTIONAL_MF", "W_NOT_FUNCTIONAL_GO_BP",
                "W_FUNCTION_INFO", "W_FUNCTIONAL_GO_MF", "W_FUNCTIONAL_GO_BP")));

        when(annotationService.findAnnotations("WO_ANNOT")).thenReturn(new ArrayList<>());

        Annotation a1 = new Annotation();
        a1.setAnnotationCategory(AnnotationCategory.GO_MOLECULAR_FUNCTION);
        a1.setCvTermAccessionCode("GO:0005524");
        when(annotationService.findAnnotations("W_NOT_FUNCTIONAL_GO_MF")).thenReturn(Collections.singletonList(a1));

        Annotation a2 = new Annotation();
        a2.setAnnotationCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        a2.setCvTermAccessionCode("GO:0051260");
        when(annotationService.findAnnotations("W_NOT_FUNCTIONAL_GO_BP")).thenReturn(Collections.singletonList(a2));

        Annotation a3 = new Annotation();
        a3.setAnnotationCategory(AnnotationCategory.FUNCTION_INFO);
        when(annotationService.findAnnotations("W_FUNCTION_INFO")).thenReturn(Collections.singletonList(a3));

        Annotation a4 = new Annotation();
        a4.setAnnotationCategory(AnnotationCategory.GO_MOLECULAR_FUNCTION);
        a4.setCvTermAccessionCode("GO:0003899");
        when(annotationService.findAnnotations("W_FUNCTIONAL_GO_MF")).thenReturn(Collections.singletonList(a4));

        Annotation a5 = new Annotation();
        a5.setAnnotationCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        a5.setCvTermAccessionCode("GO:0006379");
        when(annotationService.findAnnotations("W_FUNCTIONAL_GO_BP")).thenReturn(Collections.singletonList(a5));
    }

    @Test
    public void shouldAddAnnotationWithoutFunctionTag() {
        ReleaseStatsTag tag = releaseInfoService.getAnnotationWithoutFunctionTag();
        assertNotNull(tag);
        assertEquals(3, tag.getCount());
    }
}