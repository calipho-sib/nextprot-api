package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.HashMap;
import java.util.Map;

import static org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificityTest.mockAnnotationIsoformSpecificity;
import static org.nextprot.api.core.service.annotation.merge.SimilarGroupBuilder.AnnotationFinder.newSimilarityPredicate;

public class AnnotationSimilarityPredicateTest {

    @Test
    public void annotationsShouldBeSimilar() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation("NX_P43246", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarAccessionDiffer() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation("NX_P43247", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarDatabaseDiffer() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation("NX_P43246", "neXtProut"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldBeSimilarWithDifferentDatabaseCases() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation("NX_P43246", "nextprot"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationVariantShouldBeSimilar() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.VARIANT);

        Assert.assertTrue(predicate.isSimilar(mockVariant("NX_P43246", "nextprot", "A", "L", "NX_Q15858", 29),
                mockVariant("NX_P43246", "nextprot", "A", "L", "NX_Q15858", 29)));
    }

    @Test
    public void annotationVariantShouldNotBeSimilar() {

        AnnotationSimilarityPredicate predicate = newSimilarityPredicate(AnnotationCategory.VARIANT);

        Assert.assertFalse(predicate.isSimilar(mockVariant("NX_P43246", "nextprot", "A", "L", "NX_Q15858", 29),
                mockVariant("NX_P43246", "nextprot", "A", "L", "NX_Q15858", 39)));
    }

    private static Annotation mockAnnotation(String accession, String database) {

        Annotation annot = Mockito.mock(Annotation.class);
        BioObject bo = Mockito.mock(BioObject.class);

        Mockito.when(bo.getAccession()).thenReturn(accession);
        Mockito.when(bo.getDatabase()).thenReturn(database);

        Mockito.when(annot.getBioObject()).thenReturn(bo);

        return annot;
    }

    private static Annotation mockVariant(String accession, String database, String variant, String original, String isoName, int from) {

        Annotation annot = Mockito.mock(Annotation.class);
        BioObject bo = Mockito.mock(BioObject.class);
        AnnotationVariant av = Mockito.mock(AnnotationVariant.class);
        Map<String, AnnotationIsoformSpecificity> specificityMap = new HashMap<>();
        specificityMap.put(isoName, mockAnnotationIsoformSpecificity(isoName, from, from));

        Mockito.when(bo.getAccession()).thenReturn(accession);
        Mockito.when(bo.getDatabase()).thenReturn(database);
        Mockito.when(av.getVariant()).thenReturn(variant);
        Mockito.when(av.getOriginal()).thenReturn(original);
        Mockito.when(annot.getTargetingIsoformsMap()).thenReturn(specificityMap);

        Mockito.when(annot.getAPICategory()).thenReturn(AnnotationCategory.VARIANT);
        Mockito.when(annot.getVariant()).thenReturn(av);
        Mockito.when(annot.getBioObject()).thenReturn(bo);

        return annot;
    }
}