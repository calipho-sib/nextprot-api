package org.nextprot.api.core.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class AnnotationUtilsUnconfirmedPE1Test extends CoreUnitBaseTest {
        	
    @Test
	public void test11() {

    	ArrayList<Annotation> list = new ArrayList<>();
    	StringBuilder msg;
    	
    	// OK - non overlapping peptides both just long enough
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 18, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 38, true)); 
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);

    	// KO - non overlapping peptides 1st not long enough
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 17, true)); // size = 8
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 38, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// KO - non overlapping peptides, 2nd not long enough
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 18, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 37, true)); // size = 8
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// OK - non overlapping peptides, both just long enough plus one inclding others
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(3, "PEP_INCL", 10, 38, true)); 
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 18, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 38, true)); 
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);

    	// OK - non overlapping peptides, both just long enough plus one inclding others, in another order
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 18, true)); 
    	list.add(createPeptideMappingAnnot(3, "PEP_INCL", 10, 38, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 38, true)); 
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);

    	// OK - non overlapping peptides, both just long enough plus one inclding others, in another order
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 10, 18, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 30, 38, true)); 
    	list.add(createPeptideMappingAnnot(3, "PEP_INCL", 10, 38, true)); 
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);

    	// OK - overlapping peptides long enough and coverage just enough
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 11, 21, true));  // size = 11 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 20, 28, true));  // size = 19, coverage = 18
    	Assert.assertEquals(true, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);

    	// KO - overlapping peptides long enough but coverage too small
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 12, 22, true));  // size = 11 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 20, 28, true));  // size = 19, coverage = 17
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

      	// KO - peptides including each other
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_INCLUDING", 10, 40, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_MIDDLE", 12, 30, true)); 
    	list.add(createPeptideMappingAnnot(3, "PEP_INCLUDED", 14, 20, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// KO - same peptides but other id, exists ???
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 11, 20, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_2", 11, 20, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// KO - non overlapping peptides but same peptide id
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, "PEP_1", 11, 20, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_1", 31, 39, true)); 
    	
    	// KO - non overlapping peptides but 1 peptide without name
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, null, 11, 20, true)); 
    	list.add(createPeptideMappingAnnot(2, "PEP_1", 31, 39, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// OK - non overlapping peptides but no peptide name at all
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, null, 11, 20, true)); 
    	list.add(createPeptideMappingAnnot(2, null, 31, 39, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));
    	System.out.println(msg);
    	
    	// KO - ok but 1 annotation is not peptide  mapping annotation
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, null, 11, 20, true)); 
    	Annotation a2 = createPeptideMappingAnnot(2, null, 31, 39, true); 
    	a2.setAnnotationCategory(AnnotationCategory.PTM_INFO);
    	list.add(a2);
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// KO - ok but just 1 peptide 
    	list.clear(); msg = new StringBuilder();
    	list.add(createPeptideMappingAnnot(1, null, 11, 20, true)); 
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    	// KO - no peptide
    	list.clear(); msg = new StringBuilder();
    	Assert.assertEquals(false, AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(list, msg));

    }
	
	
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
	
	
	private Annotation createPeptideMappingAnnot(int id, Integer p1, Integer p2, boolean proteotypic) {
		return createPeptideMappingAnnot(id,"PEP_"+id,p1,p2,proteotypic);
	}
	
	private Annotation createPeptideMappingAnnot(int id, String pepname, Integer p1, Integer p2, boolean proteotypic) {

		Annotation a = new Annotation();
		a.setAnnotationCategory(AnnotationCategory.PEPTIDE_MAPPING);
		a.setAnnotationId(id);
		AnnotationProperty p = new AnnotationProperty();
		p.setName("is proteotypic");
		p.setValue(proteotypic ? "Y" : "N");
		a.addProperty(p);		
		if (pepname!=null) {
			p = new AnnotationProperty();
			p.setName("peptide name");
			p.setValue(pepname);
			a.addProperty(p);
		}
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