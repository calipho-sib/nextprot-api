package org.nextprot.api.web.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildEntryTest extends WebIntegrationBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Test
	public void testWithEnsemblGeneShouldBePresent() throws Exception {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withGenomicMappings().withChromosomalLocations());

		Assert.assertEquals(1, entry.getChromosomalLocations().size());
		Assert.assertEquals("ENSG00000254647", entry.getChromosomalLocations().get(0).getAccession());
		Assert.assertTrue(!entry.getGenomicMappings().isEmpty());
		GenomicMapping gm = entry.getGenomicMappings().iterator().next();
		Assert.assertEquals("ENSG00000254647", gm.getAccession());
		Assert.assertEquals("Ensembl", gm.getDatabase());
	}

	@Test
	public void testVirtualGeneShouldBeAbsent() throws Exception {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_Q6ZTC4").withGenomicMappings().withChromosomalLocations().withXrefs());

		Assert.assertEquals(1, entry.getChromosomalLocations().size());
		Assert.assertTrue(!entry.getChromosomalLocations().get(0).getAccession().isEmpty());
		Assert.assertTrue(entry.getGenomicMappings().isEmpty());
		for (DbXref xref : entry.getXrefs()) {
			Assert.assertTrue(!xref.getAccession().matches("NX_VG.+"));
		}
	}

	@Test
	public void testVirtualGene2ShouldBeAbsent() throws Exception {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_O00370").withGenomicMappings().withChromosomalLocations().withXrefs());

		Assert.assertEquals(1, entry.getChromosomalLocations().size());
		Assert.assertTrue(!entry.getChromosomalLocations().get(0).getAccession().isEmpty());
		Assert.assertTrue(entry.getGenomicMappings().isEmpty());
		for (DbXref xref : entry.getXrefs()) {
			Assert.assertTrue(!xref.getAccession().matches("VG.+"));
		}
	}

	@Test
	public void testNonEnsgShouldHaveUndefinedAccession() throws Exception {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_Q96PT3").withGenomicMappings().withChromosomalLocations().withXrefs());

		Assert.assertEquals(1, entry.getChromosomalLocations().size());
		Assert.assertTrue(!entry.getChromosomalLocations().get(0).getAccession().isEmpty());
		Assert.assertTrue(entry.getGenomicMappings().isEmpty());
	}
}