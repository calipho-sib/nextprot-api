package org.nextprot.api.rdf.domain;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.TestCase;

public class OWLAnnotationCategoryTest extends TestCase {

	@Test
	public void testUnknownAnnotationTypeName() {
		try {
			OWLAnnotationCategory.getByDbAnnotationTypeName("unexisting annotation type name");
			assertTrue(false);
		} catch (RuntimeException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testKnownAnnotationTypeName() {
		try {
			OWLAnnotationCategory.getByDbAnnotationTypeName("pathway");
			assertTrue(true);
		} catch (RuntimeException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testEnzymeRegulationParents() {
		OWLAnnotationCategory cat = OWLAnnotationCategory.getByDbAnnotationTypeName("enzyme regulation");
		assertTrue(cat.getParents().contains(OWLAnnotationCategory.GENERIC_INTERACTION));
		assertTrue(cat.getParents().contains(OWLAnnotationCategory.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==2);
	}
	
	@Test
	public void testPathwayParents() {
		OWLAnnotationCategory cat = OWLAnnotationCategory.getByDbAnnotationTypeName("pathway");
		assertTrue(cat.getParents().contains(OWLAnnotationCategory.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==1);
	}

	@Test
	public void testFunctionChildren() {
		OWLAnnotationCategory cat = OWLAnnotationCategory.GENERIC_FUNCTION;
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.FUNCTION_INFO));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.CATALYTIC_ACTIVITY));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.COFACTOR));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.ENZYME_REGULATION));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.PATHWAY));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.GO_BIOLOGICAL_PROCESS));
		assertTrue(cat.getChildren().contains(OWLAnnotationCategory.GO_MOLECULAR_FUNCTION));
		assertTrue(cat.getChildren().size()==7);
	}
	
	@Test
	public void testRoots() {
		assertTrue(OWLAnnotationCategory.getRoots().contains(OWLAnnotationCategory.NAME));
		assertTrue(OWLAnnotationCategory.getRoots().contains(OWLAnnotationCategory.GENERAL_ANNOTATION));
		assertTrue(OWLAnnotationCategory.getRoots().contains(OWLAnnotationCategory.POSITIONAL_ANNOTATION));
		assertTrue(OWLAnnotationCategory.getRoots().size()==3);		
	}
	
	@Test
	public void testDbAnnotationTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (OWLAnnotationCategory cat: OWLAnnotationCategory.values()) {
			if (atns.contains(cat.getDbAnnotationTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbAnnotationTypeName " + cat.getDbAnnotationTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getDbAnnotationTypeName());
			}
		}
		assertTrue(OWLAnnotationCategory.values().length==atns.size());
	}
	
	@Test
	public void testDbIdUnicity() {
		Set<Integer> atns = new HashSet<Integer>();
		for (OWLAnnotationCategory cat: OWLAnnotationCategory.values()) {
			if (atns.contains(cat.getDbId())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		assertTrue(OWLAnnotationCategory.values().length==atns.size());
	}
	
	
	@Test
	public void testRdfTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (OWLAnnotationCategory cat: OWLAnnotationCategory.values()) {
			if (atns.contains(cat.getRdfTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getRdfTypeName " + cat.getRdfTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getRdfTypeName());
			}
		}
		assertTrue(OWLAnnotationCategory.values().length==atns.size());
	}
	
	/*
	 * Enum hashCode of Enum values() is based on the memory address, 
	 * so nothing to define identity of each value
	 */
	@Test
	public void testEquals() {
		Set<OWLAnnotationCategory> s = new HashSet<OWLAnnotationCategory>();
		// add each enum values twice
		for (OWLAnnotationCategory c: OWLAnnotationCategory.values()) s.add(c);
		for (OWLAnnotationCategory c: OWLAnnotationCategory.values()) s.add(c);		
		// and then check at the end that we have only one of each in the set
		int expected = OWLAnnotationCategory.values().length;
		System.out.println("Expected number of OWLCategories = " + expected);
		assertTrue(s.size()==expected);
	}

	@Test
	public void testTopologyAllChildren() {
		Set<OWLAnnotationCategory> c = OWLAnnotationCategory.TOPOLOGY.getAllChildren();
		assertTrue(c.contains(OWLAnnotationCategory.TRANSMEMBRANE_REGION));
		assertTrue(c.contains(OWLAnnotationCategory.INTRAMEMBRANE_REGION));
		assertTrue(c.contains(OWLAnnotationCategory.TOPOLOGICAL_DOMAIN));
		assertTrue(c.size()==3);
	}
	
	@Test
	public void testTopologicalDomainAllChildren() {
		Set<OWLAnnotationCategory> c = OWLAnnotationCategory.TOPOLOGICAL_DOMAIN.getAllChildren();
		assertTrue(c.size()==0);
	}
		
	@Test
	public void testNameAllChildren() {
		Set<OWLAnnotationCategory> cs = OWLAnnotationCategory.NAME.getAllChildren();
		System.out.println("Name all children:"+cs.size());
		assertTrue(cs.contains(OWLAnnotationCategory.FAMILY_NAME));
		assertTrue(cs.contains(OWLAnnotationCategory.ENZYME_CLASSIFICATION));
		assertTrue(true);
	}
	
	
	@Test
	public void testRootsAllChildrenConsistency() {
		// set of roots
		Set<OWLAnnotationCategory> r = OWLAnnotationCategory.getRoots();
		System.out.println("Roots :"+ r.size());
		// set of children of each root
		Set<OWLAnnotationCategory> s1 = OWLAnnotationCategory.GENERAL_ANNOTATION.getAllChildren();
		System.out.println("Positional annotations :"+ s1.size());
		Set<OWLAnnotationCategory> s2 = OWLAnnotationCategory.POSITIONAL_ANNOTATION.getAllChildren();
		System.out.println("General annotations :"+ s2.size());
		Set<OWLAnnotationCategory> s3 = OWLAnnotationCategory.NAME.getAllChildren();
		System.out.println("Names :"+ s3.size());
		int count = OWLAnnotationCategory.values().length;
		System.out.println("Roots and children :"+ (r.size()+s1.size()+ s2.size()+s3.size()));
		System.out.println("Full count :"+count);
		// we assume that no child has more than one root parent (but it is not forbidden)
		// so the sum of each root shildren set + number of roots should be equal to enum values count
		assertTrue(r.size()+s1.size()+ s2.size()+s3.size()==count);
	}

	@Test
	public void testPositionalAnnotationAllParents() {
		Set<OWLAnnotationCategory> cs = OWLAnnotationCategory.POSITIONAL_ANNOTATION.getAllParents();
		assertTrue(cs.size()==0);
	}
	
	@Test
	public void testActiveSiteAllParents() {
		Set<OWLAnnotationCategory> cs = OWLAnnotationCategory.ACTIVE_SITE.getAllParents();
		assertTrue(cs.contains(OWLAnnotationCategory.GENERIC_SITE));
		assertTrue(cs.contains(OWLAnnotationCategory.POSITIONAL_ANNOTATION));
		assertTrue(cs.size()==2);
	}
	
	@Test
	public void testEnzymeRegulationAllParents() {
		Set<OWLAnnotationCategory> cs = OWLAnnotationCategory.ENZYME_REGULATION.getAllParents();
		assertTrue(cs.contains(OWLAnnotationCategory.GENERIC_INTERACTION));
		assertTrue(cs.contains(OWLAnnotationCategory.GENERIC_FUNCTION));		
		assertTrue(cs.contains(OWLAnnotationCategory.GENERAL_ANNOTATION));
		assertTrue(cs.size()==3);
	}
	
	public void show() {
		for (OWLAnnotationCategory cat : OWLAnnotationCategory.values()) 
			System.out.println(cat.toString());		
	}
	
}
