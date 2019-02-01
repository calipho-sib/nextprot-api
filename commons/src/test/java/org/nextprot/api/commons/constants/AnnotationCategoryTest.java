package org.nextprot.api.commons.constants;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationCategoryTest {


	@Test
	public void testIsLeaf() {
		Assert.assertEquals(true, AnnotationCategory.PDB_MAPPING.isLeaf());
		Assert.assertEquals(false, AnnotationCategory.GENERAL_ANNOTATION.isLeaf());
		Assert.assertEquals(true, AnnotationCategory.FUNCTION_INFO.isLeaf());
		Assert.assertEquals(false, AnnotationCategory.GENERIC_EXPRESSION.isLeaf());
		
	}

	@Test
	public void testUnknownAnnotationTypeName() {
		try {
			AnnotationCategory.getByDbAnnotationTypeName("unexisting annotation type name");
			Assert.assertTrue(false);
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testKnownAnnotationTypeName() {
		try {
			AnnotationCategory.getByDbAnnotationTypeName("pathway");
			Assert.assertTrue(true);
		} catch (RuntimeException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testUniqueParents() {

		Assert.assertNull(AnnotationCategory.ROOT.getParent());

		for (AnnotationCategory model : AnnotationCategory.values()) {

			if (model != AnnotationCategory.ROOT)
				Assert.assertNotNull(model.getParent());
		}
	}

	@Test
	public void testEnzymeRegulationParents() {
		AnnotationCategory cat = AnnotationCategory.getByDbAnnotationTypeName("activity regulation");
		Assert.assertTrue(cat.getParent() == AnnotationCategory.GENERIC_INTERACTION);
	}
	
	@Test
	public void testPathwayParents() {
		AnnotationCategory cat = AnnotationCategory.getByDbAnnotationTypeName("pathway");
		Assert.assertTrue(cat.getParent() == AnnotationCategory.GENERIC_FUNCTION);
	}

	@Test
	public void testFunctionChildren() {
		AnnotationCategory cat = AnnotationCategory.GENERIC_FUNCTION;
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.FUNCTION_INFO));
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.CATALYTIC_ACTIVITY));
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.PATHWAY));
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.GO_BIOLOGICAL_PROCESS));
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.GO_MOLECULAR_FUNCTION));
		Assert.assertTrue(cat.getChildren().contains(AnnotationCategory.TRANSPORT_ACTIVITY));
		Assert.assertEquals(6, cat.getChildren().size());
	}
	
	@Test
	public void testRootChildren() {
		Set<AnnotationCategory> children = AnnotationCategory.ROOT.getChildren();
		Assert.assertTrue(children.contains(AnnotationCategory.NAME));
		Assert.assertTrue(children.contains(AnnotationCategory.GENERAL_ANNOTATION));
		Assert.assertTrue(children.contains(AnnotationCategory.POSITIONAL_ANNOTATION));
		Assert.assertTrue(children.contains(AnnotationCategory.VIRTUAL_ANNOTATION));
		Assert.assertEquals(4, children.size());
	}
	
	@Test
	public void testDbAnnotationTypeNameUnicity() {
		Set<String> atns = new HashSet<>();
		for (AnnotationCategory cat: AnnotationCategory.values()) {
			if (!atns.contains(cat.getDbAnnotationTypeName())) {

				atns.add(cat.getDbAnnotationTypeName());
			}
		}
		Assert.assertEquals(AnnotationCategory.values().length, atns.size());
	}
	
	@Test
	public void testDbIdUnicity() {
		Set<Integer> atns = new HashSet<>();
		for (AnnotationCategory cat: AnnotationCategory.values()) {
			if (atns.contains(cat.getDbId())) {
				System.out.println("ERROR: AnnotationCategory.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		Assert.assertEquals(AnnotationCategory.values().length, atns.size());
	}

	@Test
	public void testApiTypeNameUnicity() {
		Set<String> atns = new HashSet<>();
		for (AnnotationCategory cat: AnnotationCategory.values()) {
			if (!atns.contains(cat.getApiTypeName())) {

				atns.add(cat.getApiTypeName());
			}
		}
		Assert.assertEquals(AnnotationCategory.values().length, atns.size());
	}
	
	/*
	 * Enum hashCode of Enum values() is based on the memory address, 
	 * so nothing to define identity of each value
	 */
	@Test
	public void testEquals() {
		Set<AnnotationCategory> s = new HashSet<>();
		// add each enum values twice
		for (AnnotationCategory c: AnnotationCategory.values()) s.add(c);
		for (AnnotationCategory c: AnnotationCategory.values()) s.add(c);
		// and then check at the end that we have only one of each in the set
		int expected = AnnotationCategory.values().length;
		System.out.println("Expected number of OWLCategories = " + expected);
		Assert.assertTrue(s.size() == expected);
	}
	
	//@Test
	public void testShowLeaves() {
		AnnotationCategory[] acs = AnnotationCategory.values();
		List<String> leaves = new ArrayList<String>();
		for (AnnotationCategory ac: acs) {
			if (ac.getChildren().size()==0) {
				leaves.add(ac.getAnnotationCategoryNameForXML());
			}
		}
		Collections.sort(leaves);
		for (String s: leaves) {
			//System.out.println(s);
		}
			
	}
		

	@Test
	public void testTopologyAllChildren() {
		Set<AnnotationCategory> c = AnnotationCategory.TOPOLOGY.getAllChildren();
		Assert.assertTrue(c.contains(AnnotationCategory.TRANSMEMBRANE_REGION));
		Assert.assertTrue(c.contains(AnnotationCategory.INTRAMEMBRANE_REGION));
		Assert.assertTrue(c.contains(AnnotationCategory.TOPOLOGICAL_DOMAIN));
		Assert.assertTrue(c.size() == 3);
	}
	
	@Test
	public void testTopologicalDomainAllChildren() {
		Set<AnnotationCategory> c = AnnotationCategory.TOPOLOGICAL_DOMAIN.getAllChildren();
		Assert.assertTrue(c.size() == 0);
	}
		
	@Test
	public void testNameAllChildren() {
		Set<AnnotationCategory> cs = AnnotationCategory.NAME.getAllChildren();
		Assert.assertTrue(cs.contains(AnnotationCategory.FAMILY_NAME));
		//assertTrue(cs.contains(AnnotationCategory.ENZYME_CLASSIFICATION)); // is now a child of general annotiation
	}
	
	@Test
	public void testGeneralAnnotationAllChildren() {
		// get all children of general annotation
		Set<AnnotationCategory> cs = AnnotationCategory.GENERAL_ANNOTATION.getAllChildren();
		// build a new set of children containing...
		Set<AnnotationCategory> cs2 = new HashSet<>();
		for (AnnotationCategory aam: AnnotationCategory.GENERAL_ANNOTATION.getChildren()) {
			// ... each direct child of general annotation
			cs2.add(aam);
			// ... together with direct child of each child (we assume we have two child level only)
			cs2.addAll(aam.getChildren());
		}
//		System.out.println("all children of general annotation: "+cs.size());
//		System.out.println("children of children of general annotation: "+cs2.size());
		Assert.assertTrue(cs.containsAll(cs2));
		Assert.assertTrue(cs2.containsAll(cs));
		Assert.assertTrue(cs.equals(cs2));
	}

	@Test
	public void testRootsAllChildrenConsistency() {
		// set of roots
		Set<AnnotationCategory> r = AnnotationCategory.ROOT.getChildren();
		//System.out.println("Roots :"+ r.size());
		// set of children of each root
		Set<AnnotationCategory> s1 = AnnotationCategory.GENERAL_ANNOTATION.getAllChildren();
		// System.out.println("Positional annotations :"+ s1.size());
		Set<AnnotationCategory> s2 = AnnotationCategory.POSITIONAL_ANNOTATION.getAllChildren();
		// System.out.println("General annotations :"+ s2.size());
		Set<AnnotationCategory> s3 = AnnotationCategory.NAME.getAllChildren();
		// System.out.println("Names :"+ s3.size());
		Set<AnnotationCategory> s4 = AnnotationCategory.VIRTUAL_ANNOTATION.getAllChildren();
		// System.out.println("Names :"+ s4.size());
		int count = AnnotationCategory.values().length - 1;
		// System.out.println("Roots and children :"+ (r.size()+s1.size()+ s2.size()+s3.size()+s4.size()));
		// System.out.println("Full count :" + count);
		// we assume that no child has more than one root parent (but it is not forbidden)
		// so the sum of each root shildren set + number of roots should be equal to enum values count
		Assert.assertEquals(r.size() + s1.size() + s2.size() + s3.size() + s4.size(), count);
	}

	@Test
	public void testPositionalAnnotationAllParents() {
		Set<AnnotationCategory> cs = AnnotationCategory.POSITIONAL_ANNOTATION.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationCategory.ROOT));
		Assert.assertTrue(cs.size() == 1);
	}
	
	@Test
	public void testActiveSiteAllParents() {
		Set<AnnotationCategory> cs = AnnotationCategory.ACTIVE_SITE.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationCategory.GENERIC_SITE));
		Assert.assertTrue(cs.contains(AnnotationCategory.POSITIONAL_ANNOTATION));
		Assert.assertTrue(cs.contains(AnnotationCategory.ROOT));
		Assert.assertTrue(cs.size() == 3);
	}
	
	@Test
	public void testEnzymeRegulationAllParents() {
		Set<AnnotationCategory> cs = AnnotationCategory.ACTIVITY_REGULATION.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationCategory.GENERIC_INTERACTION));
		Assert.assertTrue(cs.contains(AnnotationCategory.GENERAL_ANNOTATION));
		Assert.assertTrue(cs.contains(AnnotationCategory.ROOT));
		Assert.assertEquals(3, cs.size());
	}
	
	@Test
	public void testEnzymeRegulationAllParentsButRoot() {
		Set<AnnotationCategory> cs = AnnotationCategory.ACTIVITY_REGULATION.getAllParentsButRoot();
		Assert.assertTrue(cs.contains(AnnotationCategory.GENERIC_INTERACTION));
		Assert.assertTrue(cs.contains(AnnotationCategory.GENERAL_ANNOTATION));
		Assert.assertEquals(2, cs.size());
	}

	@Test
	public void testGetAllParents() {

		Assert.assertEquals(Sets.newHashSet(AnnotationCategory.ROOT, AnnotationCategory.POSITIONAL_ANNOTATION, AnnotationCategory.GENERIC_SITE),
				AnnotationCategory.CLEAVAGE_SITE.getAllParents());
	}

	@Test
	public void testGetAllParentsButRoot() {

		Assert.assertEquals(Sets.newHashSet(AnnotationCategory.POSITIONAL_ANNOTATION, AnnotationCategory.GENERIC_SITE),
				AnnotationCategory.CLEAVAGE_SITE.getAllParentsButRoot());
	}

	@Test
	public void testGetPathToRoot() {

		Assert.assertEquals("positional-annotation:generic-site", AnnotationCategory.ACTIVE_SITE.getPathToRoot(':'));
	}

	@Test
	public void testInstanciatedCategories() {

		Assert.assertEquals(66, AnnotationCategory.getInstantiatedCategories().size());
	}

	@Test
	public void testValueofBioPhysChem() {

		Assert.assertEquals(AnnotationCategory.ABSORPTION_MAX, AnnotationCategory.getByDbAnnotationTypeName("absorption max"));
		Assert.assertEquals(AnnotationCategory.ABSORPTION_NOTE, AnnotationCategory.getByDbAnnotationTypeName("absorption note"));
		Assert.assertEquals(AnnotationCategory.KINETIC_KM, AnnotationCategory.getByDbAnnotationTypeName("kinetic KM"));
		Assert.assertEquals(AnnotationCategory.KINETIC_NOTE, AnnotationCategory.getByDbAnnotationTypeName("kinetic note"));
		Assert.assertEquals(AnnotationCategory.KINETIC_VMAX, AnnotationCategory.getByDbAnnotationTypeName("kinetic Vmax"));
		Assert.assertEquals(AnnotationCategory.PH_DEPENDENCE, AnnotationCategory.getByDbAnnotationTypeName("pH dependence"));
		Assert.assertEquals(AnnotationCategory.REDOX_POTENTIAL, AnnotationCategory.getByDbAnnotationTypeName("redox potential"));
		Assert.assertEquals(AnnotationCategory.TEMPERATURE_DEPENDENCE, AnnotationCategory.getByDbAnnotationTypeName("temperature dependence"));
	}

	@Test
	public void testApiNamesDoesNotContainSpaces() {

		for (AnnotationCategory model : AnnotationCategory.values()) {

			Assert.assertTrue(!model.getApiTypeName().contains(" "));
		}
	}

	@Test
	public void testGetAnnotationCategoryHierarchyForXML() {

		for (AnnotationCategory model : AnnotationCategory.values()) {

            String cat = model.getAnnotationCategoryHierarchyForXML();

            Assert.assertTrue("'"+cat+"' unexpectly contains space", !cat.contains(" "));
		}
	}

    @Test
    public void testGetAnnotationCategoryNameForXML() {

        for (AnnotationCategory model : AnnotationCategory.values()) {

            String cat = model.getAnnotationCategoryNameForXML();

            Assert.assertTrue("'"+cat+"' unexpectly contains space", !cat.contains(" "));
        }
    }

	@Test
	public void shouldFindAnnotationCategory() {

		Assert.assertTrue(AnnotationCategory.hasAnnotationByApiName("ptm"));
	}

	@Test
	public void shouldNotFindSpongebobAnnotationCategory() {

		Assert.assertFalse(AnnotationCategory.hasAnnotationByApiName("spongeboo"));
	}

	// TODO: USELESS TEST
	@Test
	public void sortCategories() {

		List<AnnotationCategory> cats = AnnotationCategory.getSortedCategories();

		//System.out.println(cats);
	}//cats.stream().map(a -> a.getHierarchy()+"."+a.getApiTypeName()).collect(Collectors.toList())
}
