package org.nextprot.api.commons.constants;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public class AnnotationApiModelTest extends TestCase {

	@Test
	public void testPdbMappingProperties() {
		assertEquals(2,AnnotationApiModel.PDB_MAPPING.getProperties().size());
		assertNotNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("resolution"));
		assertNotNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("method"));
		assertNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("unexistingpropertydbname"));
	}
	@Test
	public void testDomainInfoProperties() {
		assertNull(AnnotationApiModel.DOMAIN_INFO.getProperties());
	}

	
	@Test
	public void testUnknownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("unexisting annotation type name");
			assertTrue(false);
		} catch (RuntimeException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testKnownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("pathway");
			assertTrue(true);
		} catch (RuntimeException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testEnzymeRegulationParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("enzyme regulation");
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_INTERACTION));
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==2);
	}
	
	@Test
	public void testPathwayParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("pathway");
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==1);
	}

	@Test
	public void testFunctionChildren() {
		AnnotationApiModel cat = AnnotationApiModel.GENERIC_FUNCTION;
		assertTrue(cat.getChildren().contains(AnnotationApiModel.FUNCTION_INFO));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.CATALYTIC_ACTIVITY));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.COFACTOR));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.ENZYME_REGULATION));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.PATHWAY));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_BIOLOGICAL_PROCESS));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_MOLECULAR_FUNCTION));
		assertTrue(cat.getChildren().size()==7);
	}
	
	@Test
	public void testRootChildren() {
		Set<AnnotationApiModel> children = AnnotationApiModel.ROOT.getChildren();
		assertTrue(children.contains(AnnotationApiModel.NAME));
		assertTrue(children.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		assertTrue(children.contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		assertTrue(children.size()==3);		
	}
	
	@Test
	public void testDbAnnotationTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbAnnotationTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbAnnotationTypeName " + cat.getDbAnnotationTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getDbAnnotationTypeName());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	@Test
	public void testDbIdUnicity() {
		Set<Integer> atns = new HashSet<Integer>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbId())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	
	@Test
	public void testRdfTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getRdfTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getRdfTypeName " + cat.getRdfTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getRdfTypeName());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	/*
	 * Enum hashCode of Enum values() is based on the memory address, 
	 * so nothing to define identity of each value
	 */
	@Test
	public void testEquals() {
		Set<AnnotationApiModel> s = new HashSet<AnnotationApiModel>();
		// add each enum values twice
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);		
		// and then check at the end that we have only one of each in the set
		int expected = AnnotationApiModel.values().length;
		System.out.println("Expected number of OWLCategories = " + expected);
		assertTrue(s.size()==expected);
	}

	@Test
	public void testTopologyAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGY.getAllChildren();
		assertTrue(c.contains(AnnotationApiModel.TRANSMEMBRANE_REGION));
		assertTrue(c.contains(AnnotationApiModel.INTRAMEMBRANE_REGION));
		assertTrue(c.contains(AnnotationApiModel.TOPOLOGICAL_DOMAIN));
		assertTrue(c.size()==3);
	}
	
	@Test
	public void testTopologicalDomainAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGICAL_DOMAIN.getAllChildren();
		assertTrue(c.size()==0);
	}
		
	@Test
	public void testNameAllChildren() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.NAME.getAllChildren();
		System.out.println("Name all children:"+cs.size());
		assertTrue(cs.contains(AnnotationApiModel.FAMILY_NAME));
		//assertTrue(cs.contains(AnnotationApiModel.ENZYME_CLASSIFICATION)); // is now a child of general annotiation
	}
	
	@Test
	public void testGeneralAnnotationAllChildren() {
		// get all children of general annotation
		Set<AnnotationApiModel> cs = AnnotationApiModel.GENERAL_ANNOTATION.getAllChildren();
		// build a new set of children containing...
		Set<AnnotationApiModel> cs2 = new HashSet<AnnotationApiModel>();
		for (AnnotationApiModel aam: AnnotationApiModel.GENERAL_ANNOTATION.getChildren()) {
			// ... each direct child of general annotation
			cs2.add(aam);
			// ... together with direct child of each child (we assume we have two child level only)
			cs2.addAll(aam.getChildren());
		}
		System.out.println("all children of general annotation: "+cs.size());
		System.out.println("children of children of general annotation: "+cs2.size());
		assertTrue(cs.containsAll(cs2));
		assertTrue(cs2.containsAll(cs));
		assertTrue(cs.equals(cs2));
	}
	
	
	@Test
	public void testRootsAllChildrenConsistency() {
		// set of roots
		Set<AnnotationApiModel> r = AnnotationApiModel.ROOT.getChildren();
		System.out.println("Roots :"+ r.size());
		// set of children of each root
		Set<AnnotationApiModel> s1 = AnnotationApiModel.GENERAL_ANNOTATION.getAllChildren();
		System.out.println("Positional annotations :"+ s1.size());
		Set<AnnotationApiModel> s2 = AnnotationApiModel.POSITIONAL_ANNOTATION.getAllChildren();
		System.out.println("General annotations :"+ s2.size());
		Set<AnnotationApiModel> s3 = AnnotationApiModel.NAME.getAllChildren();
		System.out.println("Names :"+ s3.size());
		int count = AnnotationApiModel.values().length - 1;
		System.out.println("Roots and children :"+ (r.size()+s1.size()+ s2.size()+s3.size()));
		System.out.println("Full count :"+count);
		// we assume that no child has more than one root parent (but it is not forbidden)
		// so the sum of each root shildren set + number of roots should be equal to enum values count
		assertTrue(r.size()+s1.size()+ s2.size()+s3.size()==count);
	}

	@Test
	public void testPositionalAnnotationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.POSITIONAL_ANNOTATION.getAllParents();
		assertTrue(cs.contains(AnnotationApiModel.ROOT));
		assertTrue(cs.size()==1);
	}
	
	@Test
	public void testActiveSiteAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ACTIVE_SITE.getAllParents();
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_SITE));
		assertTrue(cs.contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		assertTrue(cs.contains(AnnotationApiModel.ROOT));
		assertTrue(cs.size()==3);
	}
	
	@Test
	public void testEnzymeRegulationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ENZYME_REGULATION.getAllParents();
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_INTERACTION));
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_FUNCTION));		
		assertTrue(cs.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		assertTrue(cs.contains(AnnotationApiModel.ROOT));
		assertTrue(cs.size()==4);
	}
	
	public void show() {
		for (AnnotationApiModel cat : AnnotationApiModel.values()) 
			System.out.println(cat.toString());		
	}
	
}
