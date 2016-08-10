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

        AnnotationEvidence evidence = new AnnotationEvidence();
        evidence.setQualityQualifier(QualityQualifier.GOLD.name());
        evidence.setEvidenceCodeAC("ECO:0000304");
        evidence.setEvidenceCodeName("traceable author statement used in manual assertion");
        evidence.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
        evidence.setAssignedBy("PINC");

        List<Annotation> srcList = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(evidence), "ECO:0000304", "hash"));

        List<Annotation> destList = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(evidence), "ECO:0000304"));

        merger.merge(srcList, destList);

        Assert.assertEquals(1, srcList.size());
        Assert.assertEquals(1, destList.size());
        Assert.assertEquals(1, destList.get(0).getEvidences().size());

		/*
		<annotation quality="GOLD" annotation-internal-id="$annotation.getAnnotationHash()">
			<cv-term accession="GO:0006814" terminology="go-biological-process-cv">sodium ion transport</cv-term>
			<description>
				<![CDATA[ sodium ion transport ]]>
			</description>
			<evidence-list>
				<evidence is-negative="false" resource-internal-ref="726510" resource-assoc-type="evidence" quality="GOLD" resource-type="publication" source-internal-ref="PINC">
					<cv-term accession="ECO:0000304" terminology="evidence-code-ontology-cv">
						traceable author statement used in manual assertion
					</cv-term>
				</evidence>
			</evidence-list>
			<target-isoform-list>
				<target-isoform accession="NX_Q15858-1" specificity="UNKNOWN"/>
				<target-isoform accession="NX_Q15858-2" specificity="UNKNOWN"/>
				<target-isoform accession="NX_Q15858-3" specificity="UNKNOWN"/>
			</target-isoform-list>
		</annotation>
		 */
    }

    @Test
    public void testMergeTwoSameListDifferentEvidence()  {

        AnnotationEvidence evidence1 = new AnnotationEvidence();
        evidence1.setQualityQualifier(QualityQualifier.GOLD.name());
        evidence1.setEvidenceCodeAC("ECO:0000304");
        evidence1.setEvidenceCodeName("traceable author statement used in manual assertion");
        evidence1.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
        evidence1.setAssignedBy("PINC");

        AnnotationEvidence evidence2 = new AnnotationEvidence();
        evidence1.setQualityQualifier(QualityQualifier.GOLD.name());
        evidence1.setEvidenceCodeAC("ECO:0000304");
        evidence1.setEvidenceCodeName("you can trust sponge bob");
        evidence1.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
        evidence1.setAssignedBy("SPONGEBOB");

        List<Annotation> srcList = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(evidence2), "ECO:0000304", "hash"));
        List<Annotation> destList = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
                Collections.singletonList(evidence1), "ECO:0000304"));

        merger.merge(srcList, destList);

        Assert.assertEquals(1, srcList.size());
        Assert.assertEquals(1, destList.size());
        Assert.assertEquals(2, destList.get(0).getEvidences().size());
    }

    private static Annotation mockAnnotation(AnnotationCategory cat, List<AnnotationEvidence> evidences, String cvCode) {

        Annotation annotation =new Annotation();

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