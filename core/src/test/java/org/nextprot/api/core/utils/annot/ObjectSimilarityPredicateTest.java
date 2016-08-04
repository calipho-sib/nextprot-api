package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

public class ObjectSimilarityPredicateTest {

    @Test
    public void sameObjectShouldBeSimilar() throws Exception {

        SimilarityPredicate predicate = new ObjectSimilarityPredicate(Annotation::getAPICategory);

        Annotation annot = mockAnnotation(AnnotationCategory.VARIANT);

        Assert.assertTrue(predicate.isSimilar(annot, annot));
    }

    @Test
    public void shouldGetProperAnnotationObject() throws Exception {

        ObjectSimilarityPredicate predicate = new ObjectSimilarityPredicate(Annotation::getAPICategory);

        Annotation annot = mockAnnotation(AnnotationCategory.VARIANT);

        Assert.assertEquals(AnnotationCategory.VARIANT, predicate.getObject(annot));
    }

    @Test
    public void shouldBeSimilar() throws Exception {

        SimilarityPredicate predicate = new ObjectSimilarityPredicate(Annotation::getAPICategory);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation(AnnotationCategory.VARIANT), mockAnnotation(AnnotationCategory.VARIANT)));
    }

    @Test
    public void shouldBeDifferent() throws Exception {

        SimilarityPredicate predicate = new ObjectSimilarityPredicate(Annotation::getAPICategory);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation(AnnotationCategory.VARIANT), mockAnnotation(AnnotationCategory.MUTAGENESIS)));
    }

    @Test
    public void shouldBeDifferentBasedOnBioObjectEquals() throws Exception {

        SimilarityPredicate predicateBasedOnBioObjectEquals = new ObjectSimilarityPredicate(Annotation::getBioObject);

        Assert.assertFalse(predicateBasedOnBioObjectEquals.isSimilar(mockAnnotationWithBioObject("toto", BioObject.BioType.CHEMICAL),
                mockAnnotationWithBioObject("toto", BioObject.BioType.GROUP)));
    }

    @Test
    public void shouldBeSimilarBasedOnBioObjectAccessionEquals() throws Exception {

        // testing extensibility
        SimilarityPredicate predicateBasedOnBioObjectAccessionEquals = new ObjectSimilarityPredicate(Annotation::getBioObject) {
            @Override
            protected boolean match(Annotation annotation1, Annotation annotation2) {
                return ObjectSimilarityPredicate.equalObjects(((BioObject)getObject(annotation1)).getAccession(),
                        ((BioObject)getObject(annotation2)).getAccession());
            }
        };

        Assert.assertTrue(predicateBasedOnBioObjectAccessionEquals.isSimilar(mockAnnotationWithBioObject("toto", BioObject.BioType.CHEMICAL),
                mockAnnotationWithBioObject("toto", BioObject.BioType.GROUP)));
    }

    public static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }

    public static Annotation mockAnnotationWithBioObject(String accession, BioObject.BioType type) {

        Annotation annotation = Mockito.mock(Annotation.class);

        BioObject bioObject = Mockito.mock(BioObject.class);
        when(bioObject.getAccession()).thenReturn(accession);
        when(bioObject.getBioType()).thenReturn(type);

        when(annotation.getBioObject()).thenReturn(bioObject);

        return annotation;
    }
}