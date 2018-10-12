package org.nextprot.api.core.service.annotation.merge.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.annotation.merge.SimilarGroupBuilder;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.Arrays;
import java.util.List;

public class ReducedAnnotationTest {

    @Test
    public void testReducingEvidences() throws SimilarGroupBuilder.SimilarAnnotationGroup.InvalidAnnotationGroupCategoryException {

        ReducedAnnotation reducedAnnotation = new ReducedAnnotation("spongebob", newGroup(
                newAnnotation(1L, "hash1", AnnotationCategory.MODIFIED_RESIDUE, QualityQualifier.GOLD, Arrays.asList(
                        mockAnnotationEvidence(1L, "BioEditor"),
                        mockAnnotationEvidence(1L, "GlyConnect"))
                ),
                newAnnotation(2L, "hash2", AnnotationCategory.MODIFIED_RESIDUE, QualityQualifier.GOLD, Arrays.asList(
                        mockAnnotationEvidence(2L, "roudoudou"),
                        mockAnnotationEvidence(2L, "spongebob"),
                        mockAnnotationEvidence(2L, "BioEditor"))
                )
        ));

        Annotation annot = reducedAnnotation.reduce();
        Assert.assertEquals(1L, annot.getAnnotationId());
        Assert.assertEquals(4, annot.getEvidences().size());
        Assert.assertTrue(annot.getEvidences().stream().allMatch(e -> e.getAnnotationId() == 1L));
    }

    private static SimilarGroupBuilder.SimilarAnnotationGroup newGroup(Annotation annotation, Annotation... others)
            throws SimilarGroupBuilder.SimilarAnnotationGroup.InvalidAnnotationGroupCategoryException {

        SimilarGroupBuilder.SimilarAnnotationGroup group = new SimilarGroupBuilder.SimilarAnnotationGroup(annotation);

        for (Annotation other : others) {
            group.add(other);
        }

        return group;
    }

    private static AnnotationEvidence mockAnnotationEvidence(long annotId, String db) {

        AnnotationEvidence evidence = new AnnotationEvidence();

        evidence.setAnnotationId(annotId);
        evidence.setResourceDb(db);

        return evidence;
    }

    private static Annotation newAnnotation(long annotId, String annotHash, AnnotationCategory cat, QualityQualifier qq, List<AnnotationEvidence> evidences) {

        Annotation annotation = new Annotation();

        annotation.setAnnotationId(annotId);
        annotation.setAnnotationHash(annotHash);
        annotation.setAnnotationCategory(cat);
        annotation.setQualityQualifier(qq.name());
        annotation.setEvidences(evidences);

        return annotation;
    }
}