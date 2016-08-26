package org.nextprot.api.core.utils.annot.merge;

import org.junit.Assert;
import org.junit.Before;
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
public abstract class AnnotationListMergerBaseTest<T extends AnnotationListMerger> {

    private T merger;

    protected abstract T createMerger();

    @Before
    public void setUp() {

        merger = createMerger();
    }

    @Test
    public void testMergeTwoIdenticalList()  {

        List<Annotation> list1 = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.GOLD, "ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));

        List<Annotation> list2 = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.GOLD, "ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304"));

        List<Annotation> mergedList = merger.merge(list1, list2);

        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
    }

    @Test
    public void testMergeTwoSameListDifferentEvidence()  {

        List<Annotation> list1 = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.GOLD, "ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304", "hash"));
        List<Annotation> list2 = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.GOLD, "ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304"));

        List<Annotation> mergedList = merger.merge(list1, list2);

        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertEquals(QualityQualifier.GOLD.toString(), mergedList.get(0).getQualityQualifier());
    }

    @Test
    public void qualityQualifierShouldNotTurnGoldAfterMerge()  {

        List<Annotation> list1 = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.SILVER, "ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));
        List<Annotation> list2 = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.SILVER, "ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304"));

        List<Annotation> mergedList = merger.merge(list1, list2);

        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertNull(mergedList.get(0).getQualityQualifier());
    }

    @Test
    public void qualityQualifierShouldTurnGoldAfterMerge()  {

        List<Annotation> list1 = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.SILVER, "ECO:0000304", "traceable author statement used in manual assertion", "EvidenceCodeOntologyCv", "PINC")), "ECO:0000304", "hash"));
        List<Annotation> list2 = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(mockAnnotationEvidence(QualityQualifier.GOLD, "ECO:0000304", "you can trust sponge bob", "EvidenceCodeOntologyCv", "SPONGEBOB")), "ECO:0000304"));

        List<Annotation> mergedList = merger.merge(list1, list2);

        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, mergedList.size());
        Assert.assertEquals(2, mergedList.get(0).getEvidences().size());
        Assert.assertEquals(QualityQualifier.GOLD.toString(), mergedList.get(0).getQualityQualifier());
    }

    private static AnnotationEvidence mockAnnotationEvidence(QualityQualifier qq, String codeAC, String codeName, String codeOntology, String author) {

        AnnotationEvidence evidence = new AnnotationEvidence();

        evidence.setQualityQualifier(qq.toString());
        evidence.setEvidenceCodeAC(codeAC);
        evidence.setEvidenceCodeName(codeName);
        evidence.setEvidenceCodeOntology(codeOntology);
        evidence.setAssignedBy(author);

        return evidence;
    }

    private static Annotation mockAnnotation(AnnotationCategory cat, List<AnnotationEvidence> evidences, String cvCode) {

        Annotation annotation = new Annotation();

        annotation.setCategory(cat);
        annotation.setEvidences(evidences);
        annotation.setCvTermAccessionCode(cvCode);

        return annotation;
    }

    private static Annotation mockAnnotationWithHash(AnnotationCategory cat, List<AnnotationEvidence> evidences, String cvCode, String hash) {

        Annotation annotation = mockAnnotation(cat, evidences, cvCode);

        annotation.setAnnotationHash(hash);

        return annotation;
    }
}