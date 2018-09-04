package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.Collections;
import java.util.List;

/**
 * Tests that should run on all implementations of AnnotationListMerger
 */
public abstract class EnrichableAnnotationListBaseTest<T extends EnrichableAnnotationList> {

    protected abstract T createEnrichableAnnotationList(List<Annotation> original);

    @Test
    public void testMergeTwoIdenticalList()  {

        List<Annotation> external = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));

        List<Annotation> original = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304"));

        T enrichableAnnotationList = createEnrichableAnnotationList(original);
        boolean hasMerged = enrichableAnnotationList.merge(external);
        List<Annotation> mergedList = enrichableAnnotationList.getMergedAnnotations();

        Assert.assertTrue(hasMerged);
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(1, mergedList.get(0).getEvidences().size());
    }

    @Test
    public void testMergeTwoSameListDifferentEvidence()  {

        List<Annotation> external = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304", "hash"));
        List<Annotation> original = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304"));

        T enrichableAnnotationList = createEnrichableAnnotationList(original);
        boolean hasMerged = enrichableAnnotationList.merge(external);
        List<Annotation> mergedList = enrichableAnnotationList.getMergedAnnotations();

        Assert.assertTrue(hasMerged);
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertEquals(QualityQualifier.GOLD.toString(), mergedList.get(0).getQualityQualifier());
    }

    @Test
    public void qualityQualifierShouldNotTurnGoldAfterMerge()  {

        List<Annotation> external = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.SILVER,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));
        List<Annotation> original = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.SILVER,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304"));

        T enrichableAnnotationList = createEnrichableAnnotationList(original);
        boolean hasMerged = enrichableAnnotationList.merge(external);
        List<Annotation> mergedList = enrichableAnnotationList.getMergedAnnotations();

        Assert.assertTrue(hasMerged);
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertEquals(QualityQualifier.SILVER.name(), mergedList.get(0).getQualityQualifier());
    }

    @Test
    public void qualityQualifierShouldTurnGoldAfterMerge()  {

        List<Annotation> external = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.SILVER,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));
        List<Annotation> original = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD,
                Collections.singletonList(mockAnnotationEvidence("ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304"));

        T enrichableAnnotationList = createEnrichableAnnotationList(original);
        boolean hasMerged = enrichableAnnotationList.merge(external);
        List<Annotation> mergedList = enrichableAnnotationList.getMergedAnnotations();

        Assert.assertTrue(hasMerged);
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertEquals(QualityQualifier.GOLD.toString(), mergedList.get(0).getQualityQualifier());
    }

    private static AnnotationEvidence mockAnnotationEvidence(String codeAC, String codeName, String codeOntology, String author) {

        AnnotationEvidence evidence = new AnnotationEvidence();

        evidence.setEvidenceCodeAC(codeAC);
        evidence.setEvidenceCodeName(codeName);
        evidence.setEvidenceCodeOntology(codeOntology);
        evidence.setAssignedBy(author);

        return evidence;
    }

    private static Annotation mockAnnotation(AnnotationCategory cat, QualityQualifier qq, List<AnnotationEvidence> evidences, String cvCode) {

        Annotation annotation = new Annotation();

        annotation.setAnnotationCategory(cat);
        annotation.setQualityQualifier(qq.name());
        annotation.setEvidences(evidences);
        annotation.setCvTermAccessionCode(cvCode);

        return annotation;
    }

    private static Annotation mockAnnotationWithHash(AnnotationCategory cat, QualityQualifier qq, List<AnnotationEvidence> evidences, String cvCode, String hash) {

        Annotation annotation = mockAnnotation(cat, qq, evidences, cvCode);

        annotation.setAnnotationHash(hash);

        return annotation;
    }
}