package org.nextprot.api.core.utils.annot.comp;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioGenericObject;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;
import static org.nextprot.api.core.utils.annot.comp.ByAnnotationSubjectComparatorTest.*;

public class ByAnnotationBioObjectComparatorTest {

    @Test
    public void compareAnnotationsSameBioObjects() throws Exception {

        Annotation refAnnot1 = new Annotation();
        refAnnot1.setCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        refAnnot1.setAnnotationHash("010a85141bf174a186aaf714676cb397");
        refAnnot1.setCvTermName("transmission of nerve impulse");

        Annotation refAnnot2 = new Annotation();
        refAnnot2.setCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        refAnnot2.setAnnotationHash("3bffa5c7f436f45d5c9a2aec4757a492");
        refAnnot2.setCvTermName("transmission of nerve impulse");

        ByAnnotationBioObjectComparator comparator =
                new ByAnnotationBioObjectComparator(newHashMap(refAnnot1, refAnnot2));

        Annotation annotation1 = new Annotation();
        annotation1.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "010a85141bf174a186aaf714676cb397"));

        Annotation annotation2 = new Annotation();
        annotation2.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "3bffa5c7f436f45d5c9a2aec4757a492"));

        int cmp = comparator.compare(annotation1, annotation2);
        Assert.assertEquals(0, cmp);
    }

    @Test
    public void compareAnnotationsDiffCat() throws Exception {

        Annotation refAnnot1 = new Annotation();
        refAnnot1.setCategory(AnnotationCategory.GO_CELLULAR_COMPONENT);
        refAnnot1.setAnnotationHash("010a85141bf174a186aaf714676cb397");
        refAnnot1.setCvTermName("transmission of nerve impulse");

        Annotation refAnnot2 = new Annotation();
        refAnnot2.setCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        refAnnot2.setAnnotationHash("3bffa5c7f436f45d5c9a2aec4757a492");
        refAnnot2.setCvTermName("spike train");

        ByAnnotationBioObjectComparator comparator =
                new ByAnnotationBioObjectComparator(newHashMap(refAnnot1, refAnnot2));

        Annotation annotation1 = new Annotation();
        annotation1.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "010a85141bf174a186aaf714676cb397"));

        Annotation annotation2 = new Annotation();
        annotation2.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "3bffa5c7f436f45d5c9a2aec4757a492"));

        int cmp = comparator.compare(annotation1, annotation2);
        Assert.assertEquals(1, cmp);
    }

    @Test
    public void compareAnnotationsSameCatDiffCvName() throws Exception {

        Annotation refAnnot1 = new Annotation();
        refAnnot1.setCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        refAnnot1.setAnnotationHash("010a85141bf174a186aaf714676cb397");
        refAnnot1.setCvTermName("transmission of nerve impulse");

        Annotation refAnnot2 = new Annotation();
        refAnnot2.setCategory(AnnotationCategory.GO_BIOLOGICAL_PROCESS);
        refAnnot2.setAnnotationHash("3bffa5c7f436f45d5c9a2aec4757a492");
        refAnnot2.setCvTermName("spike train");

        ByAnnotationBioObjectComparator comparator =
                new ByAnnotationBioObjectComparator(newHashMap(refAnnot1, refAnnot2));

        Annotation annotation1 = new Annotation();
        annotation1.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "010a85141bf174a186aaf714676cb397"));

        Annotation annotation2 = new Annotation();
        annotation2.setBioObject(mockBioObject(BioObject.BioType.ENTRY_ANNOTATION, BioObject.ResourceType.EXTERNAL, "3bffa5c7f436f45d5c9a2aec4757a492"));

        int cmp = comparator.compare(annotation1, annotation2);
        Assert.assertEquals(1, cmp);
    }

    private static BioGenericObject mockBioObject(BioObject.BioType bioType, BioObject.ResourceType resourceType, String hash) {

        BioGenericObject bgo = Mockito.mock(BioGenericObject.class);

        when(bgo.getBioType()).thenReturn(bioType);
        when(bgo.getResourceType()).thenReturn(resourceType);
        when(bgo.getAnnotationHash()).thenReturn(hash);

        return bgo;
    }

}