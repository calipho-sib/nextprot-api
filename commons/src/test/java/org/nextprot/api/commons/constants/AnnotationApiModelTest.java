package org.nextprot.api.commons.constants;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationApiModelTest {

	@Test
	public void testPdbMappingProperties() {
		Assert.assertEquals(2, AnnotationApiModel.PDB_MAPPING.getProperties().size());
		Assert.assertNotNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("resolution"));
		Assert.assertNotNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("method"));
		Assert.assertNull(AnnotationApiModel.PDB_MAPPING.getPropertyByDbName("unexistingpropertydbname"));
	}

	@Test
	public void testDomainInfoProperties() {
		Assert.assertNull(AnnotationApiModel.DOMAIN_INFO.getProperties());
	}

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
				System.out.println("ERROR: OWLAnnotationCategory.getDbAnnotationTypeName " + cat.getDbAnnotationTypeName() +  " is not unique" );
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
				System.out.println("ERROR: OWLAnnotationCategory.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		Assert.assertTrue(AnnotationApiModel.values().length == atns.size());
	}

	@Test
	public void testRdfTypeNameUnicity() {
		Set<String> atns = new HashSet<>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getRdfTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getRdfTypeName " + cat.getRdfTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getRdfTypeName());
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
		System.out.println("Full count :"+count);
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

		Assert.assertEquals(85, categories.size());
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

		String expected = "graph annotationTypes {\n" +
				"\tnodesep=0.1; ranksep=0.5; ratio=compress; size=\"7.5,10\"; center=true; node [style=\"rounded,filled\", width=0, height=0, shape=box, fillcolor=\"#E5E5E5\", concentrate=true]\n" +
				"\tRoot -- Name ;\n" +
				"\tName -- FamilyName ;\n" +
				"\tRoot -- GeneralAnnotation ;\n" +
				"\tGeneralAnnotation -- CellularComponent ;\n" +
				"\tCellularComponent -- SubcellularLocation ;\n" +
				"\tCellularComponent -- GoCellularComponent ;\n" +
				"\tCellularComponent -- SubcellularLocationNote ;\n" +
				"\tGeneralAnnotation -- Interaction ;\n" +
				"\tInteraction -- InteractionInfo ;\n" +
				"\tInteraction -- EnzymeRegulation ;\n" +
				"\tInteraction -- SmallMoleculeInteraction ;\n" +
				"\tInteraction -- Cofactor ;\n" +
				"\tInteraction -- BinaryInteraction ;\n" +
				"\tGeneralAnnotation -- EnzymeClassification ;\n" +
				"\tGeneralAnnotation -- Caution ;\n" +
				"\tGeneralAnnotation -- Miscellaneous ;\n" +
				"\tGeneralAnnotation -- Function ;\n" +
				"\tFunction -- GoMolecularFunction ;\n" +
				"\tFunction -- CatalyticActivity ;\n" +
				"\tFunction -- FunctionInfo ;\n" +
				"\tFunction -- GoBiologicalProcess ;\n" +
				"\tFunction -- Pathway ;\n" +
				"\tGeneralAnnotation -- Induction ;\n" +
				"\tGeneralAnnotation -- Expression ;\n" +
				"\tExpression -- ExpressionInfo ;\n" +
				"\tExpression -- ExpressionProfile ;\n" +
				"\tExpression -- DevelopmentalStageInfo ;\n" +
				"\tGeneralAnnotation -- Medical ;\n" +
				"\tMedical -- Allergen ;\n" +
				"\tMedical -- Pharmaceutical ;\n" +
				"\tMedical -- Disease ;\n" +
				"\tGeneralAnnotation -- SequenceCaution ;\n" +
				"\tGeneralAnnotation -- Keyword ;\n" +
				"\tKeyword -- UniprotKeyword ;\n" +
				"\tRoot -- PositionalAnnotation ;\n" +
				"\tPositionalAnnotation -- SequenceConflict ;\n" +
				"\tPositionalAnnotation -- Site ;\n" +
				"\tSite -- MiscellaneousSite ;\n" +
				"\tSite -- ActiveSite ;\n" +
				"\tSite -- BindingSite ;\n" +
				"\tSite -- CleavageSite ;\n" +
				"\tSite -- MetalBindingSite ;\n" +
				"\tPositionalAnnotation -- NonTerminalResidue ;\n" +
				"\tPositionalAnnotation -- Topology ;\n" +
				"\tTopology -- IntramembraneRegion ;\n" +
				"\tTopology -- TopologicalDomain ;\n" +
				"\tTopology -- TransmembraneRegion ;\n" +
				"\tPositionalAnnotation -- Ptm ;\n" +
				"\tPtm -- LipidationSite ;\n" +
				"\tPtm -- CrossLink ;\n" +
				"\tPtm -- Selenocysteine ;\n" +
				"\tPtm -- ModifiedResidue ;\n" +
				"\tPtm -- DisulfideBond ;\n" +
				"\tPtm -- GlycosylationSite ;\n" +
				"\tPtm -- PtmInfo ;\n" +
				"\tPositionalAnnotation -- ProcessingProduct ;\n" +
				"\tProcessingProduct -- MitochondrialTransitPeptide ;\n" +
				"\tProcessingProduct -- MatureProtein ;\n" +
				"\tProcessingProduct -- SignalPeptide ;\n" +
				"\tProcessingProduct -- Propeptide ;\n" +
				"\tProcessingProduct -- InitiatorMethionine ;\n" +
				"\tProcessingProduct -- PeroxisomeTransitPeptide ;\n" +
				"\tPositionalAnnotation -- Region ;\n" +
				"\tRegion -- MiscellaneousRegion ;\n" +
				"\tRegion -- CoiledCoilRegion ;\n" +
				"\tRegion -- Domain ;\n" +
				"\tRegion -- NucleotidePhosphateBindingRegion ;\n" +
				"\tRegion -- CompositionallyBiasedRegion ;\n" +
				"\tRegion -- ShortSequenceMotif ;\n" +
				"\tRegion -- DnaBindingRegion ;\n" +
				"\tRegion -- CalciumBindingRegion ;\n" +
				"\tRegion -- ZincFingerRegion ;\n" +
				"\tRegion -- InteractingRegion ;\n" +
				"\tRegion -- Repeat ;\n" +
				"\tPositionalAnnotation -- Mapping ;\n" +
				"\tMapping -- PdbMapping ;\n" +
				"\tPositionalAnnotation -- SecondaryStructure ;\n" +
				"\tSecondaryStructure -- Turn ;\n" +
				"\tSecondaryStructure -- BetaStrand ;\n" +
				"\tSecondaryStructure -- Helix ;\n" +
				"\tPositionalAnnotation -- Variant ;\n" +
				"\tPositionalAnnotation -- DomainInfo ;\n" +
				"\tPositionalAnnotation -- NonConsecutiveResidue ;\n" +
				"\tPositionalAnnotation -- Mutagenesis ;\n" +
				"\tPositionalAnnotation -- VariantInfo ;\n" +
				"}";

		//Assert.assertEquals(expected, AnnotationApiModel.exportHierarchyAsGraphDot());
	}
}
