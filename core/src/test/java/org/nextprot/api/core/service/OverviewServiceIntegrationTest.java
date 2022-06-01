package org.nextprot.api.core.service;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.utils.CollectionTester;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles({ "dev","cache" })
public class OverviewServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private OverviewService overviewService;

	private void displayEntityName(String prefix, int indent, EntityName name) {
		
		String pfx = prefix;
		for (int i=0;i<indent;i++) pfx= "-"+pfx;
		if (pfx.length()>0) {
			while (pfx.length()<16) pfx += " ";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(pfx + name.getId());		
		sb.append(" - " + "parentId: " + name.getParentId());
		sb.append(" - " + name.getClazz());
		sb.append(" - "  + name.getQualifier() + " - " + name.getCategory() + 
				" - " + name.getComposedName() + " - " + name.getType());
		sb.append(" = " + name.getName());
		System.out.println(sb.toString());
		for (EntityName syn : name.getSynonyms()) displayEntityName( "synonym", indent +1, syn);
		for (EntityName oth : name.getOtherRecommendedEntityNames()) displayEntityName("other", indent +1, oth);
	}
	
	@Test
	public void testNamesForQ13043() {
		// temp code for new tests
		Overview overview = overviewService.findOverviewByEntry("NX_Q13043");
		for (EntityName name : overview.getProteinNames()) displayEntityName("main", 0, name);
		for (EntityName name : overview.getAlternativeProteinNames()) displayEntityName("alt", 0, name);
		for (EntityName name : overview.getAdditionalNames()) displayEntityName("add", 0, name);
		for (EntityName name : overview.getFunctionalRegionNames()) displayEntityName("fun", 0, name);
		for (EntityName name : overview.getCleavedRegionNames()) displayEntityName("cleav", 0, name);
	}
	
	
	@Test
	public void testNamesForQ86X52() {

		Overview overview = overviewService.findOverviewByEntry("NX_Q86X52");

		/// protein names
		EntityName recName = overview.getRecommendedProteinName();
		assertEntityNameEquals(recName, Overview.EntityNameClass.PROTEIN_NAMES, "PR_645672", "Chondroitin sulfate synthase 1");

		// recommended names
		Assert.assertTrue(new EntityNameCollectionTester(recName.getOtherRecommendedEntityNames()).contains(Arrays.asList(
				mockEntityName( "2.4.1.175", "EC", "EC"),
				mockEntityName("2.4.1.226", "EC", "EC")
			)
		));

		// synonyms
		Assert.assertTrue(new EntityNameCollectionTester(recName.getSynonyms()).contains(Collections.emptyList()));

		// alternative names
		Assert.assertTrue(new EntityNameCollectionTester(overview.getAlternativeProteinNames()).contains(Arrays.asList(
				mockEntityName( "Chondroitin glucuronyltransferase 1", "protein", "full"),
				mockEntityNameWithSynonyms("Chondroitin synthase 1", "protein", "full", Arrays.asList(mockEntityName( "ChSy-1", "protein", "short"))),
				mockEntityName( "Glucuronosyl-N-acetylgalactosaminyl-proteoglycan 4-beta-N-acetylgalactosaminyltransferase 1", "protein", "full"),
				mockEntityName( "N-acetylgalactosaminyl-proteoglycan 3-beta-glucuronosyltransferase 1", "protein", "full"),
				mockEntityName("N-acetylgalactosaminyltransferase 1", "protein", "full")
				)
		));

		/// gene names
		List<EntityName> geneNames = overview.getGeneNames();

		Assert.assertEquals(1, geneNames.size());
		assertEntityNameEquals(geneNames.get(0), Overview.EntityNameClass.GENE_NAMES, "PR_1162684", "CHSY1");

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getOtherRecommendedEntityNames()).contains(Collections.<EntityName>emptyList()));

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getSynonyms()).contains(
				Arrays.asList(
						mockEntityName( "CHSY", "gene name"),
						mockEntityName("CSS1", "gene name"),
						mockEntityName( "KIAA0990", "gene name"),
						mockEntityName("UNQ756/PRO1487", "ORF")
				)
		));
	}

	@Test
	public void testNamesForQ3L8U1() {

		Overview overview = overviewService.findOverviewByEntry("NX_Q3L8U1");

		/// protein names
		EntityName recName = overview.getRecommendedProteinName();
		assertEntityNameEquals(recName, Overview.EntityNameClass.PROTEIN_NAMES, "PR_699748", "Chromodomain-helicase-DNA-binding protein 9");

		// recommended names
		Assert.assertTrue(new EntityNameCollectionTester(recName.getOtherRecommendedEntityNames()).contains(Arrays.asList(
				mockEntityName( "3.6.4.12", "EC", "EC")
			)
		));

		// synonyms
		Assert.assertTrue(new EntityNameCollectionTester(recName.getSynonyms()).contains(Arrays.asList(
				mockEntityName("CHD-9", "protein", "short")
			)
		));

		// alternative names
		Assert.assertTrue(new EntityNameCollectionTester(overview.getAlternativeProteinNames()).contains(Arrays.asList(
				mockEntityName( "ATP-dependent helicase CHD9", "protein", "full"),
				mockEntityNameWithSynonyms( "Chromatin-related mesenchymal modulator", "protein", "full", Arrays.asList(mockEntityName( "CReMM", "protein", "short"))),
				mockEntityName( "Chromatin-remodeling factor CHROM1", "protein", "full"),
				mockEntityName( "Kismet homolog 2", "protein", "full"),
				mockEntityName( "Peroxisomal proliferator-activated receptor A-interacting complex 320 kDa protein", "protein", "full"),
				mockEntityName("PPAR-alpha-interacting complex protein 320 kDa", "protein", "full")
				)
		));

		/// gene names
		List<EntityName> geneNames = overview.getGeneNames();
		Assert.assertEquals(1, geneNames.size());

		assertEntityNameEquals(geneNames.get(0), Overview.EntityNameClass.GENE_NAMES, "PR_1181044", "CHD9");
		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getOtherRecommendedEntityNames()).contains(Collections.<EntityName>emptyList()));

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getSynonyms()).contains(Arrays.asList(
						mockEntityName("KIAA0308", "gene name"),
						mockEntityName("KISH2", "gene name"),
						mockEntityName( "PRIC320", "gene name"),
						mockEntityName( "AD-013", "ORF"),
						mockEntityName("x0008", "ORF")
				)
		));
	}

	@Test
	public void testChainNamesForP51659() {

		Overview overview = overviewService.findOverviewByEntry("NX_P51659");

		/// chain names
		List<EntityName> chainNames = overview.getCleavedRegionNames();
		Assert.assertEquals(2, chainNames.size());

		Assert.assertTrue(new EntityNameCollectionTester(chainNames).contains(Arrays.asList(
				mockEntityNameWithOtherRecNames("(3R)-hydroxyacyl-CoA dehydrogenase", "protein", "full",
						Collections.singletonList(
							mockEntityName( "1.1.1.n12", "chain", "EC")
						)
				),
				mockEntityNameWithSynonymsAndOtherRecNames( "Enoyl-CoA hydratase 2", "protein", "full",
						Collections.singletonList(
							mockEntityName( "3-alpha,7-alpha,12-alpha-trihydroxy-5-beta-cholest-24-enoyl-CoA hydratase", "chain", "full")
						),
						Arrays.asList(
							mockEntityName("4.2.1.107", "chain", "EC"),
							mockEntityName( "4.2.1.119", "chain", "EC")
						)
				)
		)));
	}

	@Test
	public void testFunctionalRegionsForP26439() {

		Overview overview = overviewService.findOverviewByEntry("NX_P26439");

		List<EntityName> regionNames = overview.getFunctionalRegionNames();
		Assert.assertEquals(2, regionNames.size());

		Assert.assertTrue(new EntityNameCollectionTester(regionNames).contains(Arrays.asList(
				mockEntityNameWithSynonymsAndOtherRecNames( "Steroid Delta-isomerase", "region name", "full", Collections.singletonList(
						mockEntityName( "Delta-5-3-ketosteroid isomerase", "region name", "full")
				), Collections.singletonList(
						mockEntityName( "5.3.3.1", "EC", "EC")
				)),
				mockEntityNameWithSynonymsAndOtherRecNames( "3-beta-hydroxy-Delta(5)-steroid dehydrogenase", "region name", "full", Arrays.asList(
						mockEntityName("3-beta-hydroxy-5-ene steroid dehydrogenase", "region name", "full"),
						mockEntityName("Progesterone reductase", "region name", "full")
				), Collections.singletonList(
						mockEntityName("1.1.1.145", "EC", "EC")
				))
		)));

	}

	private static EntityName mockEntityName(String name, String category) {

		return mockEntityName(name, category, null);
	}

	private static EntityName mockEntityName(String name, String category, String qualifier) {

		EntityName entityName = Mockito.mock(EntityName.class);

		Mockito.when(entityName.getQualifier()).thenReturn(qualifier);
		Mockito.when(entityName.getCategory()).thenReturn(category);
		Mockito.when(entityName.getName()).thenReturn(name);
		Mockito.when(entityName.getSynonyms()).thenReturn(Collections.<EntityName>emptyList());

		return entityName;
	}

	private static EntityName mockEntityNameWithSynonyms(String name, String category, String qualifier, List<EntityName> synonyms) {

		EntityName entityName = mockEntityName(name, category, qualifier);

		Mockito.when(entityName.getSynonyms()).thenReturn(synonyms);

		return entityName;
	}

	private static EntityName mockEntityNameWithOtherRecNames(String name, String category, String qualifier, List<EntityName> recNames) {

		EntityName entityName = mockEntityName(name, category, qualifier);

		Mockito.when(entityName.getOtherRecommendedEntityNames()).thenReturn(recNames);

		return entityName;
	}

	private static EntityName mockEntityNameWithSynonymsAndOtherRecNames(String name, String category, String qualifier, List<EntityName> synonyms, List<EntityName> recNames) {

		EntityName entityName = mockEntityName(name, category, qualifier);

		Mockito.when(entityName.getSynonyms()).thenReturn(synonyms);
		Mockito.when(entityName.getOtherRecommendedEntityNames()).thenReturn(recNames);

		return entityName;
	}

	private void assertEntityNameEquals(EntityName entityName, Overview.EntityNameClass expectedClass, String expectedId, String expectedName) {

		Assert.assertTrue(entityName.isMain());
		Assert.assertEquals(expectedClass, entityName.getClazz());
		Assert.assertEquals(expectedName, entityName.getName());
	}

	private static class EntityNameCollectionTester extends CollectionTester<EntityName, String> {

		EntityNameCollectionTester(Collection<EntityName> observedCollection) {
			super(observedCollection);
		}

		@Override
		protected Function<EntityName, String> createElementToKeyFunc() {
			return entityName -> entityName.getName();
		}

		@Override
		protected boolean isEquals(EntityName element, EntityName expected) {

			return expected.getName().equals(element.getName()) &&
					expected.getCategory().equals(element.getCategory()) &&
					Objects.equals(expected.getQualifier(), element.getQualifier()) &&
					hasSynonyms(element, expected) &&
					hasRecommendedNames(element, expected);
		}

		private boolean hasSynonyms(EntityName element, EntityName expected) {

			EntityNameCollectionSimpleTester synonymTester = new EntityNameCollectionSimpleTester(element.getSynonyms());

			for (EntityName expectedSynonym : expected.getSynonyms()) {

				if (!synonymTester.contains(expectedSynonym))
					return false;
			}

			return true;
		}

		private boolean hasRecommendedNames(EntityName element, EntityName expected) {

			EntityNameCollectionSimpleTester simpleTester = new EntityNameCollectionSimpleTester(element.getOtherRecommendedEntityNames());

			for (EntityName expectedRecName : expected.getOtherRecommendedEntityNames()) {

				if (!simpleTester.contains(expectedRecName))
					return false;
			}

			return true;
		}
	}

	private static class EntityNameCollectionSimpleTester extends CollectionTester<EntityName, String> {

		EntityNameCollectionSimpleTester(Collection<EntityName> synonyms) {
			super(synonyms);
		}

		@Override
		protected Function<EntityName, String> createElementToKeyFunc() {
			return EntityName::getName;
		}

		@Override
		protected boolean isEquals(EntityName element, EntityName expected) {

			return expected.getName().equals(element.getName()) &&
					expected.getCategory().equals(element.getCategory());
		}
	}
}
