package org.nextprot.api.web.service.impl;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContentWithPlaceHoldersTest {

	@Test
	public void shouldFoundPlaceHolders() {

		String text = "#Copyright notice for neXtProt\n" +
				"\n" +
				"neXtProt is developed by the SIB Swiss Institute of Bioinformatics. \n" +
				"\n" +
				"All intellectual property rights on neXtProt, belong to the SIB.\n" +
				"\n" +
				"Copyright &copy; 2010-${COPYRIGHT_END_DATE} SIB Swiss Institute of Bioinformatics &dash; All rights reserved.\n" +
				"\n" +
				"```\n" +
				"Swiss Institute of Bioinformatics\n" +
				"CMU - 1 Rue Michel-Servet\n" +
				"CH-1211 Geneva\n" +
				"Switzerland\n" +
				"```\n" +
				"\n" +
				"neXtProt provides links to several resources. We recommend to our users to read carefully the copyright notices and legal disclaimer of the said resources. \n" +
				"\n" +
				"neXtProt uses the following third parties software:\n" +
				"\n" +
				"**Data stores**\n" +
				"\n" +
				"* [PostgreSQL](http://www.postgresql.org): Database where sequences, annotations, evidences and terms, as well as user resources (profiles, saved lists and queries) are stored.\n" +
				"* [Lucene](http://lucene.apache.org)/[solr](http://lucene.apache.org/solr): Full-text search engine for simple search queries.\n" +
				"* [Virtuoso](http://virtuoso.openlinksw.com): To store RDF data and perform complex SPARQL search queries.\n" +
				"\n" +
				"**API**\n" +
				"\n" +
				"* [Spring](http://spring.io): Open source web application over Java. \n" +
				"\n" +
				"**User interface**\n" +
				"\n" +
				"* [AngularJS](https://angularjs.org/): Javascript framework for website interface. ";

		Assert.assertTrue(ContentWithPlaceHolders.foundPlaceHolders(text));
	}
}