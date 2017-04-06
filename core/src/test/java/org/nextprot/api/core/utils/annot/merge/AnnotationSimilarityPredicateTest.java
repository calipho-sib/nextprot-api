package org.nextprot.api.core.utils.annot.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Optional;

public class AnnotationSimilarityPredicateTest {

    @Test
    public void newSimilarityPredicateShouldReturnNewInstanceIfDefinedCategory() throws Exception {

        Assert.assertTrue(AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.VARIANT).isPresent());
    }

    @Test
    public void newSimilarityPredicateShouldReturnNullIfNotDefinedForCategory() throws Exception {

        Assert.assertTrue(!AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.ANTIBODY_MAPPING).isPresent());
    }

    @Test
    public void annotationsShouldBeSimilar() throws Exception {

        Optional<AnnotationSimilarityPredicate> predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isPresent());

        Assert.assertTrue(predicate.get().isSimilar(mockAnnotation("NX_P43246", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarAccessionDiffer() throws Exception {

        Optional<AnnotationSimilarityPredicate> predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isPresent());

        Assert.assertFalse(predicate.get().isSimilar(mockAnnotation("NX_P43247", "neXtProt"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldNotBeSimilarDatabaseDiffer() throws Exception {

        Optional<AnnotationSimilarityPredicate> predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isPresent());

        Assert.assertFalse(predicate.get().isSimilar(mockAnnotation("NX_P43246", "neXtProut"), mockAnnotation("NX_P43246", "neXtProt")));
    }

    @Test
    public void annotationsShouldBeSimilarWithDifferentDatabaseCases() throws Exception {

        Optional<AnnotationSimilarityPredicate> predicate = AnnotationSimilarityPredicate.newSimilarityPredicate(AnnotationCategory.BINARY_INTERACTION);

        Assert.assertTrue(predicate.isPresent());

        Assert.assertTrue(predicate.get().isSimilar(mockAnnotation("NX_P43246", "nextprot"), mockAnnotation("NX_P43246", "neXtProt")));
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