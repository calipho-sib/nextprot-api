package org.nextprot.api.solr.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml","classpath:spring/solr-context.xml"})
public class SolrCoreRepositoryTest {

	@Autowired
	SolrCoreRepository repository;

	@Test
	public void testSolrCoreTerm() {

		testSolrCore(SolrCore.Alias.Term, "npcvs1", "http://crick:8983/solr");
	}

	@Test
	public void testSolrCorePubli() {

		testSolrCore(SolrCore.Alias.Publication, "nppublications1", "http://crick:8983/solr");
	}

	@Test
	public void testSolrCoreEntry() {

		testSolrCore(SolrCore.Alias.Entry, "npentries1", "http://crick:8983/solr");
	}

	@Test
	public void testSolrCoreEntryGold() {

		testSolrCore(SolrCore.Alias.GoldEntry, "npentries1gold", "http://crick:8983/solr");
	}

	private void testSolrCore(SolrCore.Alias alias, String expectedCoreName, String expectedBaseURL) {

		Assert.assertTrue(repository.hasSolrCore(alias.getName()));

		SolrCore repo = repository.getSolrCore(alias);

		Assert.assertEquals(expectedCoreName, repo.getName());
		Assert.assertEquals(alias, repo.getAlias());
		SolrHttpClient defaultSolrClient = repo.newSolrClient();
		Assert.assertEquals(expectedBaseURL, defaultSolrClient.getBaseURL());
		Assert.assertEquals(expectedBaseURL+"/"+expectedCoreName, defaultSolrClient.getURL());
	}
}