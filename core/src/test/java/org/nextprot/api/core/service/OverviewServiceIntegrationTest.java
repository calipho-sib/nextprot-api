package org.nextprot.api.core.service;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.utils.CollectionTester;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles({ "dev" })
public class OverviewServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private OverviewService overviewService;

	@Test
	public void testNamesForQ86X52() {

		Overview overview = overviewService.findOverviewByEntry("NX_Q86X52");

		/// protein names
		EntityName recName = overview.getRecommendedProteinName();
		assertEntityNameEquals(recName, Overview.EntityNameClass.PROTEIN_NAMES, "PR_645672", "Chondroitin sulfate synthase 1");

		// recommended names
		Assert.assertTrue(new EntityNameCollectionTester(recName.getOtherRecommendedEntityNames()).contains(Arrays.asList(
				mockEntityName("PR_2477891", "2.4.1.175", "EC", "EC"),
				mockEntityName("PR_2477890", "2.4.1.226", "EC", "EC")
			)
		));

		// synonyms
		Assert.assertTrue(new EntityNameCollectionTester(recName.getSynonyms()).contains(Collections.<EntityName>emptyList()));

		// alternative names
		Assert.assertTrue(new EntityNameCollectionTester(overview.getAlternativeProteinNames()).contains(Arrays.asList(
				mockEntityName("PR_645671", "Chondroitin glucuronyltransferase 1", "protein", "full"),
				mockEntityNameWithSynonyms("PR_645668", "Chondroitin synthase 1", "protein", "full", Arrays.asList(mockEntityName("PR_645674", "ChSy-1", "protein", "short"))),
				mockEntityName("PR_645673", "Glucuronosyl-N-acetylgalactosaminyl-proteoglycan 4-beta-N-acetylgalactosaminyltransferase 1", "protein", "full"),
				mockEntityName("PR_645667", "N-acetylgalactosaminyl-proteoglycan 3-beta-glucuronosyltransferase 1", "protein", "full"),
				mockEntityName("PR_645677", "N-acetylgalactosaminyltransferase 1", "protein", "full")
				)
		));

		/// gene names
		List<EntityName> geneNames = overview.getGeneNames();

		Assert.assertEquals(1, geneNames.size());
		assertEntityNameEquals(geneNames.get(0), Overview.EntityNameClass.GENE_NAMES, "PR_1162684", "CHSY1");

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getOtherRecommendedEntityNames()).contains(Collections.<EntityName>emptyList()));

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getSynonyms()).contains(
				Arrays.asList(
						mockEntityName("PR_1162682", "CHSY", "gene name"),
						mockEntityName("PR_1162681", "CSS1", "gene name"),
						mockEntityName("PR_1162683", "KIAA0990", "gene name"),
						mockEntityName("PR_1162685", "UNQ756/PRO1487", "ORF")
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
				mockEntityName("PR_2472030", "3.6.4.12", "EC", "EC")
			)
		));

		// synonyms
		Assert.assertTrue(new EntityNameCollectionTester(recName.getSynonyms()).contains(Arrays.asList(
				mockEntityName("PR_699735", "CHD-9", "protein", "short")
			)
		));

		// alternative names
		Assert.assertTrue(new EntityNameCollectionTester(overview.getAlternativeProteinNames()).contains(Arrays.asList(
				mockEntityName("PR_699741", "ATP-dependent helicase CHD9", "protein", "full"),
				mockEntityNameWithSynonyms("PR_699745", "Chromatin-related mesenchymal modulator", "protein", "full", Arrays.asList(mockEntityName("PR_699733", "CReMM", "protein", "short"))),
				mockEntityName("PR_699739", "Chromatin-remodeling factor CHROM1", "protein", "full"),
				mockEntityName("PR_699737", "Kismet homolog 2", "protein", "full"),
				mockEntityName("PR_699731", "Peroxisomal proliferator-activated receptor A-interacting complex 320 kDa protein", "protein", "full"),
				mockEntityName("PR_699742", "PPAR-alpha-interacting complex protein 320 kDa", "protein", "full")
				)
		));

		/// gene names
		List<EntityName> geneNames = overview.getGeneNames();
		Assert.assertEquals(1, geneNames.size());

		assertEntityNameEquals(geneNames.get(0), Overview.EntityNameClass.GENE_NAMES, "PR_1181044", "CHD9");
		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getOtherRecommendedEntityNames()).contains(Collections.<EntityName>emptyList()));

		Assert.assertTrue(new EntityNameCollectionTester(geneNames.get(0).getSynonyms()).contains(Arrays.asList(
						mockEntityName("PR_1181043", "KIAA0308", "gene name"),
						mockEntityName("PR_1181046", "KISH2", "gene name"),
						mockEntityName("PR_1181048", "PRIC320", "gene name"),
						mockEntityName("PR_1181047", "AD-013", "ORF"),
						mockEntityName("PR_1181045", "x0008", "ORF")
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
				mockEntityNameWithOtherRecNames("MP_12154770", "(3R)-hydroxyacyl-CoA dehydrogenase", "protein", "full", Collections.singletonList(
						mockEntityName("MP_12154769", "1.1.1.n12", "chain", "EC")
				)),
				mockEntityNameWithSynonymsAndOtherRecNames("MP_12154767", "Enoyl-CoA hydratase 2", "protein", "full", Collections.singletonList(
						mockEntityName("MP_12154766", "3-alpha,7-alpha,12-alpha-trihydroxy-5-beta-cholest-24-enoyl-CoA hydratase", "chain", "full")
				), Arrays.asList(
						mockEntityName("MP_12154768", "4.2.1.107", "chain", "EC"),
						mockEntityName("MP_12154765", "4.2.1.119", "chain", "EC")
				))
		)));
	}

	private static EntityName mockEntityName(String id, String name, String category) {

		return mockEntityName(id, name, category, null);
	}

	private static EntityName mockEntityName(String id, String name, String category, String qualifier) {

		EntityName entityName = Mockito.mock(EntityName.class);

		Mockito.when(entityName.getId()).thenReturn(id);
		Mockito.when(entityName.getQualifier()).thenReturn(qualifier);
		Mockito.when(entityName.getCategory()).thenReturn(category);
		Mockito.when(entityName.getName()).thenReturn(name);
		Mockito.when(entityName.getSynonyms()).thenReturn(Collections.<EntityName>emptyList());

		return entityName;
	}

	private static EntityName mockEntityNameWithSynonyms(String id, String name, String category, String qualifier, List<EntityName> synonyms) {

		EntityName entityName = mockEntityName(id, name, category, qualifier);

		Mockito.when(entityName.getSynonyms()).thenReturn(synonyms);

		return entityName;
	}

	private static EntityName mockEntityNameWithOtherRecNames(String id, String name, String category, String qualifier, List<EntityName> recNames) {

		EntityName entityName = mockEntityName(id, name, category, qualifier);

		Mockito.when(entityName.getOtherRecommendedEntityNames()).thenReturn(recNames);

		return entityName;
	}

	private static EntityName mockEntityNameWithSynonymsAndOtherRecNames(String id, String name, String category, String qualifier, List<EntityName> synonyms, List<EntityName> recNames) {

		EntityName entityName = mockEntityName(id, name, category, qualifier);

		Mockito.when(entityName.getSynonyms()).thenReturn(synonyms);
		Mockito.when(entityName.getOtherRecommendedEntityNames()).thenReturn(recNames);

		return entityName;
	}

	private void assertEntityNameEquals(EntityName entityName, Overview.EntityNameClass expectedClass, String expectedId, String expectedName) {

		Assert.assertTrue(entityName.isMain());
		Assert.assertEquals(expectedClass, entityName.getClazz());
		Assert.assertEquals(expectedId, entityName.getId());
		Assert.assertEquals(expectedName, entityName.getName());
	}

	private static class EntityNameCollectionTester extends CollectionTester<EntityName, String> {

		EntityNameCollectionTester(Collection<EntityName> observedCollection) {
			super(observedCollection);
		}

		@Override
		protected Function<EntityName, String> createElementToKeyFunc() {
			return new Function<EntityName, String>() {
				@Override
				public String apply(EntityName entityName) {
					return entityName.getId();
				}
			};
		}

		@Override
		protected boolean isEquals(EntityName element, EntityName expected) {

			return expected.getId().equals(element.getId()) &&
					expected.getName().equals(element.getName()) &&
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
			return new Function<EntityName, String>() {
				@Override
				public String apply(EntityName entityName) {
					return entityName.getId();
				}
			};
		}

		@Override
		protected boolean isEquals(EntityName element, EntityName expected) {

			return expected.getId().equals(element.getId()) &&
					expected.getName().equals(element.getName()) &&
					expected.getCategory().equals(element.getCategory());
		}
	}
}
