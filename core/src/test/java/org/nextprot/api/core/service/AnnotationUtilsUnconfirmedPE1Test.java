package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles({ "dev" })
public class AnnotationUtilsUnconfirmedPE1Test extends CoreUnitBaseTest {
        
    //@Autowired private EntryBuilderService entryBuilderService = null;

	
    @Test
	public void test3() {
    	ArrayList<Annotation> list = new ArrayList<>();
    	list.add(createPeptideMappingAnnot(1, 11, 20, true)); // size = 10
    	list.add(createPeptideMappingAnnot(1, 21, 29, true)); // size =  9
    	list.add(createPeptideMappingAnnot(1, 31, 38, true)); // size =  8
    	list.add(createPeptideMappingAnnot(1, 41, 47, true)); // size =  7
    	list.add(createPeptideMappingAnnot(1, 51, 56, true)); // size =  6
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 1, 9));
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 2, 9));
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 3, 9));
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 3, 7));
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 4, 7));
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 5, 7));
    }
    
    @Test
	public void test2() {
    	Assert.assertEquals(new Integer(11), AnnotationUtils.getFeatureSize(createPeptideMappingAnnot(1, 10, 20, true)));
    	Assert.assertEquals(null, AnnotationUtils.getFeatureSize(createPeptideMappingAnnot(1, null, 20, true)));
    	Assert.assertEquals(null, AnnotationUtils.getFeatureSize(createPeptideMappingAnnot(1, 10, null, true)));
    }
	
    @Test
	public void test1() {
    	
    	int minPepSize=7;
    	ArrayList<Annotation> list = new ArrayList<>();
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(1, 10, 20, true), list, minPepSize); // should be added
    	Assert.assertEquals(1, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(2, 9, 21, false), list, minPepSize); // should be ignored because not proteotypic
    	Assert.assertEquals(1, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(3, 30, 35, true), list, minPepSize); // should be ignored because size < 7
    	Assert.assertEquals(1, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(4, 30, 36, true), list, minPepSize); // should be added because size >= 7
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(4, list.get(1).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(5, 30, 40, true), list, minPepSize); // should replace 4 because includes 4
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(5, list.get(1).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(6, 30, 40, true), list, minPepSize); // should be ignored because same as 5
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(5, list.get(1).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(7, 30, 41, true), list, minPepSize); // should replace 5 because includes 5
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(7, list.get(1).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(8, 29, 41, true), list, minPepSize); // should replace 7 because includes 7
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(8, list.get(1).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(9, 33, 44, true), list, minPepSize); // should be added cos not included of another
    	Assert.assertEquals(3, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(8, list.get(1).getAnnotationId());
    	Assert.assertEquals(9, list.get(2).getAnnotationId());
    	
    	AnnotationUtils.addToNonInclusivePeptideMappingList(createPeptideMappingAnnot(10, 25, 45, true), list, minPepSize); // should replace 8 and 9 cos includes them
    	Assert.assertEquals(2, list.size());
    	Assert.assertEquals(1, list.get(0).getAnnotationId());
    	Assert.assertEquals(10, list.get(1).getAnnotationId());
    	
    }
	
	private Annotation createPeptideMappingAnnot(int id, Integer p1, Integer p2, boolean proteotypic) {
		Annotation a = new Annotation();
		a.setAnnotationId(id);
		AnnotationProperty p = new AnnotationProperty();
		p.setName("is proteotypic");
		p.setValue(proteotypic ? "Y" : "N");
		a.addProperty(p);
		AnnotationIsoformSpecificity spec1 = new AnnotationIsoformSpecificity();
		spec1.setIsoformAccession("NX_P10000-1");
		spec1.setFirstPosition(p1);
		spec1.setLastPosition(p2);
		AnnotationIsoformSpecificity spec2 = new AnnotationIsoformSpecificity();
		spec2.setIsoformAccession("NX_P10000-2");
		spec2.setFirstPosition(p1==null ? null : p1+10);
		spec2.setLastPosition(p2==null ? null : p2+10);
		List<AnnotationIsoformSpecificity> specs = new ArrayList<>();
		specs.add(spec1);
		specs.add(spec2);
		a.addTargetingIsoforms(specs);
		return a;
	}
    
    
    
    
}