package org.nextprot.api.solr.core;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
@ContextConfiguration("classpath:spring/solr-context.xml")
public class SolrCoreRepositoryTest {

	@Autowired
	SolrCoreRepository repository;

	@Test
	public void testSolrCoreTerm() {

		testSolrCore(SolrCore.Entity.Term, "npcvs1", "http://crick:8983/solr/npcvs1");
	}

	@Test
	public void testSolrCorePubli() {

		testSolrCore(SolrCore.Entity.Publication, "nppublications1", "http://crick:8983/solr/nppublications1");
	}

	@Test
	public void testSolrCoreEntry() {

		testSolrCore(SolrCore.Entity.Entry, "npentries1", "http://crick:8983/solr/npentries1");
	}

	@Test
	public void testSolrCoreEntryGold() {

		testSolrCore(SolrCore.Entity.GoldEntry, "npentries1gold", "http://crick:8983/solr/npentries1gold");
	}

	private void testSolrCore(SolrCore.Entity entity, String expectedCoreName, String expectedUrl) {

		Assert.assertTrue(repository.hasSolrCore(entity.getName()));

		SolrCore repo = repository.getSolrCore(entity);

		Assert.assertEquals(expectedCoreName, repo.getName());
		Assert.assertEquals(entity, repo.getEntity());
		Assert.assertEquals(expectedUrl, repo.getUrl());
		HttpSolrServer solrServer = repo.newHttpSolrServer();
		Assert.assertEquals(expectedUrl, solrServer.getBaseURL());
	}
}