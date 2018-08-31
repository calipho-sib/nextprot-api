package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;

public class AnnotationSimilarityPredicateTest {

    @Test
    public void annotationsShouldBeSimilar() {

        AnnotationSimilarityPredicate predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation("NX_P43246", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarAccessionDiffer() {

        AnnotationSimilarityPredicate predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation("NX_P43247", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarDatabaseDiffer() {

        AnnotationSimilarityPredicate predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation("NX_P43246", "neXtProut"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldBeSimilarWithDifferentDatabaseCases() {

        AnnotationSimilarityPredicate predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation("NX_P43246", "nextprot"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    private static Annotation mockAnnotation(String accession, String database) {

        Annotation annot = Mockito.mock(Annotation.class);
        BioObject bo = Mockito.mock(BioObject.class);

        Mockito.when(bo.getAccession()).thenReturn(accession);
        Mockito.when(bo.getDatabase()).thenReturn(database);

        Mockito.when(annot.getBioObject()).thenReturn(bo);

        return annot;
    }
}