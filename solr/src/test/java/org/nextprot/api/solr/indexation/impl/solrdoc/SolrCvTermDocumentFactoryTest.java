package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class SolrCvTermDocumentFactoryTest {

	@Autowired
	private TerminologyService terminologyService;

	@Test
	public void checkSolrDocEmptinessForSpecificOntologies() {

		SolrCvTermDocumentFactory factory = new SolrCvTermDocumentFactory();

		for (String ontology : new String[]{"OrganelleCv", "NextprotAnnotationCv", "UniprotFamilyCv"}) {

			CvTerm cvTerm = new CvTerm();
			cvTerm.setOntology(ontology);

			SolrInputDocument doc = factory.createSolrInputDocument(cvTerm);
			Assert.assertTrue(doc.isEmpty());
		}
	}

	@Test
	public void testTS0564SolrDocBuilding() {

		SolrCvTermDocumentFactory factory = new SolrCvTermDocumentFactory();

		CvTerm cvTerm = terminologyService.findCvTermByAccession("TS-0564");

		SolrInputDocument doc = factory.createSolrInputDocument(cvTerm);
		Assert.assertTrue(!doc.isEmpty());
		Assert.assertEquals(8, doc.size());
		Assert.assertEquals(38827L, doc.getFieldValue(CvSolrField.ID.getName()));
		Assert.assertEquals("TS-0564", doc.getFieldValue(CvSolrField.AC.getName()));
		Assert.assertEquals("aanptissues", doc.getFieldValue(CvSolrField.FILTERS.getName()));
		Assert.assertEquals("Liver", doc.getFieldValue(CvSolrField.NAME.getName()));
		Assert.assertEquals("liver", doc.getFieldValue(CvSolrField.NAME_S.getName()));
		Assert.assertEquals("A triangular-shaped organ located under the diaphragm in the right hypochondrium. It is the largest internal organ of the body, weighting up to 2 kg. Metabolism and bile secretion are its main functions. It is composed of cells which have the ability to regenerate.", doc.getFieldValue(CvSolrField.DESCRIPTION.getName()));
		Assert.assertEquals("Hepatic", doc.getFieldValue(CvSolrField.SYNONYMS.getName()));
		Assert.assertEquals("UBERON:0002107, Uberon:UBERON:0002107 | FMA:7197, FMA:FMA:7197 | BTO:0000759, BRENDA:BTO:0000759 | D008099, MeSH:D008099", doc.getFieldValue(CvSolrField.OTHER_XREFS.getName()));
	}
}