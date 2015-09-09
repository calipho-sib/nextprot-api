package org.nextprot.api.commons.constants;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationApiModelTest {


	@Test
	public void testIsLeaf() {
		Assert.assertEquals(true, AnnotationApiModel.PDB_MAPPING.isLeaf());
		Assert.assertEquals(false, AnnotationApiModel.GENERAL_ANNOTATION.isLeaf());
		Assert.assertEquals(true, AnnotationApiModel.FUNCTION_INFO.isLeaf());
		Assert.assertEquals(false, AnnotationApiModel.GENERIC_EXPRESSION.isLeaf());
		
	}

	@Test
	public void testUnknownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("unexisting annotation type name");
			Assert.assertTrue(false);
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testKnownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("pathway");
			Assert.assertTrue(true);
		} catch (RuntimeException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testUniqueParents() {

		Assert.assertNull(AnnotationApiModel.ROOT.getParent());

		for (AnnotationApiModel model : AnnotationApiModel.values()) {

			if (model != AnnotationApiModel.ROOT)
				Assert.assertNotNull(model.getParent());
		}
	}

	@Test
	public void testEnzymeRegulationParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("enzyme regulation");
		Assert.assertTrue(cat.getParent() == AnnotationApiModel.GENERIC_INTERACTION);
	}
	
	@Test
	public void testPathwayParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("pathway");
		Assert.assertTrue(cat.getParent() == AnnotationApiModel.GENERIC_FUNCTION);
	}

	@Test
	public void testFunctionChildren() {
		AnnotationApiModel cat = AnnotationApiModel.GENERIC_FUNCTION;
		Assert.assertTrue(cat.getChildren().contains(AnnotationApiModel.FUNCTION_INFO));
		Assert.assertTrue(cat.getChildren().contains(AnnotationApiModel.CATALYTIC_ACTIVITY));
		Assert.assertTrue(cat.getChildren().contains(AnnotationApiModel.PATHWAY));
		Assert.assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_BIOLOGICAL_PROCESS));
		Assert.assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_MOLECULAR_FUNCTION));
		Assert.assertEquals(5, cat.getChildren().size());
	}
	
	@Test
	public void testRootChildren() {
		Set<AnnotationApiModel> children = AnnotationApiModel.ROOT.getChildren();
		Assert.assertTrue(children.contains(AnnotationApiModel.NAME));
		Assert.assertTrue(children.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		Assert.assertTrue(children.contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		Assert.assertTrue(children.size() == 3);
	}
	
	@Test
	public void testDbAnnotationTypeNameUnicity() {
		Set<String> atns = new HashSet<>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbAnnotationTypeName())) {
				System.out.println("ERROR: AnnotationApiModel.getDbAnnotationTypeName " + cat.getDbAnnotationTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getDbAnnotationTypeName());
			}
		}
		Assert.assertTrue(AnnotationApiModel.values().length == atns.size());
	}
	
	@Test
	public void testDbIdUnicity() {
		Set<Integer> atns = new HashSet<>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbId())) {
				System.out.println("ERROR: AnnotationApiModel.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		Assert.assertTrue(AnnotationApiModel.values().length == atns.size());
	}

	@Test
	public void testApiTypeNameUnicity() {
		Set<String> atns = new HashSet<>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getApiTypeName())) {
				System.out.println("ERROR: AnnotationApiModel.getApiTypeName " + cat.getApiTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getApiTypeName());
			}
		}
		Assert.assertTrue(AnnotationApiModel.values().length == atns.size());
	}
	
	/*
	 * Enum hashCode of Enum values() is based on the memory address, 
	 * so nothing to define identity of each value
	 */
	@Test
	public void testEquals() {
		Set<AnnotationApiModel> s = new HashSet<>();
		// add each enum values twice
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);		
		// and then check at the end that we have only one of each in the set
		int expected = AnnotationApiModel.values().length;
		System.out.println("Expected number of OWLCategories = " + expected);
		Assert.assertTrue(s.size() == expected);
	}

	@Test
	public void testTopologyAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGY.getAllChildren();
		Assert.assertTrue(c.contains(AnnotationApiModel.TRANSMEMBRANE_REGION));
		Assert.assertTrue(c.contains(AnnotationApiModel.INTRAMEMBRANE_REGION));
		Assert.assertTrue(c.contains(AnnotationApiModel.TOPOLOGICAL_DOMAIN));
		Assert.assertTrue(c.size() == 3);
	}
	
	@Test
	public void testTopologicalDomainAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGICAL_DOMAIN.getAllChildren();
		Assert.assertTrue(c.size() == 0);
	}
		
	@Test
	public void testNameAllChildren() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.NAME.getAllChildren();
		System.out.println("Name all children:"+cs.size());
		Assert.assertTrue(cs.contains(AnnotationApiModel.FAMILY_NAME));
		//assertTrue(cs.contains(AnnotationApiModel.ENZYME_CLASSIFICATION)); // is now a child of general annotiation
	}
	
	@Test
	public void testGeneralAnnotationAllChildren() {
		// get all children of general annotation
		Set<AnnotationApiModel> cs = AnnotationApiModel.GENERAL_ANNOTATION.getAllChildren();
		// build a new set of children containing...
		Set<AnnotationApiModel> cs2 = new HashSet<>();
		for (AnnotationApiModel aam: AnnotationApiModel.GENERAL_ANNOTATION.getChildren()) {
			// ... each direct child of general annotation
			cs2.add(aam);
			// ... together with direct child of each child (we assume we have two child level only)
			cs2.addAll(aam.getChildren());
		}
		System.out.println("all children of general annotation: "+cs.size());
		System.out.println("children of children of general annotation: "+cs2.size());
		Assert.assertTrue(cs.containsAll(cs2));
		Assert.assertTrue(cs2.containsAll(cs));
		Assert.assertTrue(cs.equals(cs2));
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
		System.out.println("Full count :" + count);
		// we assume that no child has more than one root parent (but it is not forbidden)
		// so the sum of each root shildren set + number of roots should be equal to enum values count
		Assert.assertTrue(r.size() + s1.size() + s2.size() + s3.size() == count);
	}

	@Test
	public void testPositionalAnnotationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.POSITIONAL_ANNOTATION.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationApiModel.ROOT));
		Assert.assertTrue(cs.size() == 1);
	}
	
	@Test
	public void testActiveSiteAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ACTIVE_SITE.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationApiModel.GENERIC_SITE));
		Assert.assertTrue(cs.contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		Assert.assertTrue(cs.contains(AnnotationApiModel.ROOT));
		Assert.assertTrue(cs.size() == 3);
	}
	
	@Test
	public void testEnzymeRegulationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ENZYME_REGULATION.getAllParents();
		Assert.assertTrue(cs.contains(AnnotationApiModel.GENERIC_INTERACTION));
		Assert.assertTrue(cs.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		Assert.assertTrue(cs.contains(AnnotationApiModel.ROOT));
		Assert.assertEquals(3, cs.size());
	}
	
	@Test
	public void testEnzymeRegulationAllParentsButRoot() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ENZYME_REGULATION.getAllParentsButRoot();
		Assert.assertTrue(cs.contains(AnnotationApiModel.GENERIC_INTERACTION));
		Assert.assertTrue(cs.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		Assert.assertEquals(2, cs.size());
	}

	@Test
	public void testGetSortedAnnotationCategories() {

		List<AnnotationApiModel> categories = AnnotationApiModel.getSortedCategories();

		Assert.assertEquals(96, categories.size());
	}

	@Test
	public void testGetAllParents() {

		Assert.assertEquals(Sets.newHashSet(AnnotationApiModel.ROOT, AnnotationApiModel.POSITIONAL_ANNOTATION, AnnotationApiModel.GENERIC_SITE),
				AnnotationApiModel.CLEAVAGE_SITE.getAllParents());
	}

	@Test
	public void testGetAllParentsButRoot() {

		Assert.assertEquals(Sets.newHashSet(AnnotationApiModel.POSITIONAL_ANNOTATION, AnnotationApiModel.GENERIC_SITE),
				AnnotationApiModel.CLEAVAGE_SITE.getAllParentsButRoot());
	}

	@Test
	public void testGetPathToRoot() {

		Assert.assertEquals("positional-annotation:generic-site", AnnotationApiModel.ACTIVE_SITE.getPathToRoot(':'));
	}

	@Test
	public void testInstanciatedCategories() {

		Assert.assertEquals(64, AnnotationApiModel.getInstantiatedCategories().size());
	}

	@Test
	public void testExportDotTree() {

		String[] expectedEdges = new String[] {
				"Root -- Name",
				"Name -- FamilyName",
				"Root -- GeneralAnnotation",
				"GeneralAnnotation -- CellularComponent",
				"CellularComponent -- SubcellularLocation",
				"CellularComponent -- GoCellularComponent",
				"CellularComponent -- SubcellularLocationNote",
				"GeneralAnnotation -- Interaction",
				"Interaction -- InteractionInfo",
				"Interaction -- EnzymeRegulation",
				"Interaction -- SmallMoleculeInteraction",
				"Interaction -- Cofactor",
				"Interaction -- BinaryInteraction",
				"GeneralAnnotation -- EnzymeClassification",
				"GeneralAnnotation -- Caution",
				"GeneralAnnotation -- Miscellaneous",
				"GeneralAnnotation -- Function",
				"Function -- GoMolecularFunction",
				"Function -- CatalyticActivity",
				"Function -- FunctionInfo",
				"Function -- GoBiologicalProcess",
				"Function -- Pathway",
				"GeneralAnnotation -- Induction",
				"GeneralAnnotation -- Expression",
				"Expression -- ExpressionInfo",
				"Expression -- ExpressionProfile",
				"Expression -- DevelopmentalStageInfo",
				"GeneralAnnotation -- Medical",
				"Medical -- Allergen",
				"Medical -- Pharmaceutical",
				"Medical -- Disease",
				"GeneralAnnotation -- SequenceCaution",
				"GeneralAnnotation -- Keyword",
				"Keyword -- UniprotKeyword",
				"Root -- PositionalAnnotation",
				"PositionalAnnotation -- SequenceConflict",
				"PositionalAnnotation -- Site",
				"Site -- MiscellaneousSite",
				"Site -- ActiveSite",
				"Site -- BindingSite",
				"Site -- CleavageSite",
				"Site -- MetalBindingSite",
				"PositionalAnnotation -- NonTerminalResidue",
				"PositionalAnnotation -- Topology",
				"Topology -- IntramembraneRegion",
				"Topology -- TopologicalDomain",
				"Topology -- TransmembraneRegion",
				"PositionalAnnotation -- Ptm",
				"Ptm -- LipidationSite",
				"Ptm -- CrossLink",
				"Ptm -- Selenocysteine",
				"Ptm -- ModifiedResidue",
				"Ptm -- DisulfideBond",
				"Ptm -- GlycosylationSite",
				"Ptm -- PtmInfo",
				"PositionalAnnotation -- ProcessingProduct",
				"ProcessingProduct -- MitochondrialTransitPeptide",
				"ProcessingProduct -- MatureProtein",
				"ProcessingProduct -- SignalPeptide",
				"ProcessingProduct -- Propeptide",
				"ProcessingProduct -- InitiatorMethionine",
				"ProcessingProduct -- PeroxisomeTransitPeptide",
				"PositionalAnnotation -- Region",
				"Region -- MiscellaneousRegion",
				"Region -- CoiledCoilRegion",
				"Region -- Domain",
				"Region -- NucleotidePhosphateBindingRegion",
				"Region -- CompositionallyBiasedRegion",
				"Region -- ShortSequenceMotif",
				"Region -- DnaBindingRegion",
				"Region -- CalciumBindingRegion",
				"Region -- ZincFingerRegion",
				"Region -- InteractingRegion",
				"Region -- Repeat",
				"PositionalAnnotation -- Mapping",
				"Mapping -- PdbMapping",
				"PositionalAnnotation -- SecondaryStructure",
				"SecondaryStructure -- Turn",
				"SecondaryStructure -- BetaStrand",
				"SecondaryStructure -- Helix",
				"PositionalAnnotation -- Variant",
				"PositionalAnnotation -- DomainInfo",
				"PositionalAnnotation -- NonConsecutiveResidue",
				"PositionalAnnotation -- Mutagenesis",
				"PositionalAnnotation -- VariantInfo"
		};

		String export = AnnotationApiModel.exportHierarchyAsGraphDot();

		for (String expectedEdge : expectedEdges) {
			Assert.assertTrue(export.contains(expectedEdge));
		}
	}

	@Test
	public void testValueofBioPhysChem() {

		Assert.assertEquals(AnnotationApiModel.ABSORPTION_MAX, AnnotationApiModel.getByDbAnnotationTypeName("absorption max"));
		Assert.assertEquals(AnnotationApiModel.ABSORPTION_NOTE, AnnotationApiModel.getByDbAnnotationTypeName("absorption note"));
		Assert.assertEquals(AnnotationApiModel.KINETIC_KM, AnnotationApiModel.getByDbAnnotationTypeName("kinetic KM"));
		Assert.assertEquals(AnnotationApiModel.KINETIC_NOTE, AnnotationApiModel.getByDbAnnotationTypeName("kinetic note"));
		Assert.assertEquals(AnnotationApiModel.KINETIC_VMAX, AnnotationApiModel.getByDbAnnotationTypeName("kinetic Vmax"));
		Assert.assertEquals(AnnotationApiModel.PH_DEPENDENCE, AnnotationApiModel.getByDbAnnotationTypeName("pH dependence"));
		Assert.assertEquals(AnnotationApiModel.REDOX_POTENTIAL, AnnotationApiModel.getByDbAnnotationTypeName("redox potential"));
		Assert.assertEquals(AnnotationApiModel.TEMPERATURE_DEPENDENCE, AnnotationApiModel.getByDbAnnotationTypeName("temperature dependence"));
	}

	@Test
	public void testApiNamesDoesNotContainSpaces() {

		for (AnnotationApiModel model : AnnotationApiModel.values()) {

			Assert.assertTrue(!model.getApiTypeName().contains(" "));
		}
	}
}
