package org.nextprot.api.solr.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.solr.core.impl.cores.SolrCoreNew;
import org.nextprot.api.solr.core.impl.cores.SolrCoreRepositoryNew;
import org.nextprot.api.solr.core.impl.cores.SolrServerNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml","classpath:spring/solr-context.xml"})
public class SolrCoreNewRepositoryTest {

	@Autowired
	SolrCoreRepositoryNew repository;

	@Test
	public void testSolrCoreTerm() {

		testSolrCore(SolrCoreNew.Alias.Term, "npcvs1", "http://crick:8983/solr/npcvs1");
	}

	@Test
	public void testSolrCorePubli() {

		testSolrCore(SolrCoreNew.Alias.Publication, "nppublications1", "http://crick:8983/solr/nppublications1");
	}

	@Test
	public void testSolrCoreEntry() {

		testSolrCore(SolrCoreNew.Alias.Entry, "npentries1", "http://crick:8983/solr/npentries1");
	}

	@Test
	public void testSolrCoreEntryGold() {

		testSolrCore(SolrCoreNew.Alias.GoldEntry, "npentries1gold", "http://crick:8983/solr/npentries1gold");
	}

	private void testSolrCore(SolrCoreNew.Alias alias, String expectedCoreName, String expectedUrl) {

		Assert.assertTrue(repository.hasSolrCore(alias.getName()));

		SolrCoreNew repo = repository.getSolrCore(alias);

		Assert.assertEquals(expectedCoreName, repo.getName());
		Assert.assertEquals(alias, repo.getAlias());
		SolrServerNew defaultSolrServer = repo.newSolrServer();
		Assert.assertEquals(expectedUrl, defaultSolrServer.getURL());
	}
}