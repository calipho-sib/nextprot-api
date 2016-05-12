package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.CvIndex.CvField;
import org.nextprot.api.tasks.solr.indexer.CvTermSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.springframework.beans.factory.annotation.Autowired;

public class CVtermCoreFullDiffTest extends SolrDiffTest {

	@Autowired TerminologyService terminologyService;
	
	SolrIndexer<CvTerm> cvindexer = new CvTermSolrIndexer("http://localhost:8983/solr/npcvs1");

	@Test
	public void testCVs() {
		//List<Terminology> allterms = terminologyService.findTerminologyByOntology("UniprotFamilyCv"); //.findAllTerminology();
		//System.err.println(allterms.size() + " terms");
		//for(int i=0; i < allterms.size(); i++){ 	if(i%1000 == 0) System.err.println(i + "..."); testCVData(allterms.get(i)); } 
		
		//test samples from different CVs
		testCVData(terminologyService.findTerminologyByAccession("CVCL_1286"));
		testCVData(terminologyService.findTerminologyByAccession("CVCA_0010"));
		testCVData(terminologyService.findTerminologyByAccession("CVME_0007"));
		testCVData(terminologyService.findTerminologyByAccession("FA-00011"));
		testCVData(terminologyService.findTerminologyByAccession("CVTO_0008"));
		testCVData(terminologyService.findTerminologyByAccession("2.1.1.293"));
		testCVData(terminologyService.findTerminologyByAccession("TS-1154"));
		testCVData(terminologyService.findTerminologyByAccession("UPA00483"));
		testCVData(terminologyService.findTerminologyByAccession("176400")); // OMIM
		testCVData(terminologyService.findTerminologyByAccession("SL-0015"));
		testCVData(terminologyService.findTerminologyByAccession("KW-0597"));
		testCVData(terminologyService.findTerminologyByAccession("D029242")); // Mesh
		testCVData(terminologyService.findTerminologyByAccession("PTM-0254"));
		testCVData(terminologyService.findTerminologyByAccession("GO:0005044"));
		testCVData(terminologyService.findTerminologyByAccession("GO:1990722"));
		testCVData(terminologyService.findTerminologyByAccession("GO:1901926"));
		testCVData(terminologyService.findTerminologyByAccession("DI-01854"));
		testCVData(terminologyService.findTerminologyByAccession("HsapDO:0000037"));
		testCVData(terminologyService.findTerminologyByAccession("C115440")); // NCI thesaurus
		testCVData(terminologyService.findTerminologyByAccession("EV:0300000"));
		testCVData(terminologyService.findTerminologyByAccession("DO-00859")); 
	}

	
	public void testCVData(CvTerm term) {
		
		long id = term.getId();
		String entry = term.getAccession();
		
		if(id == 154329) return;
		System.out.println("Testing cv: " + Long.toString(id) + "=" + entry);
		SolrInputDocument solrDoc = cvindexer.convertToSolrDocument(term);
		
		String expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.AC);
		//System.err.println("expected ac: " + expected);
		Assert.assertEquals(expected.trim(), solrDoc.getFieldValue("ac"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.FILTERS);
		//System.err.println("expected filters: " + expected + " -> " + solrDoc.getFieldValue("filters"));
		Assert.assertEquals(expected, solrDoc.getFieldValue("filters"));

		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.NAME);
		//System.err.println("name: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("name"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.NAME_S);
		//System.err.println("name_s: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("name_s"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.DESCRIPTION);
		//System.err.println("first_page: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("description"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.OTHER_XREFS);
		//System.err.println("other_xrefs: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("other_xrefs"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(Long.toString(id), CvField.PROPERTIES);
		//System.err.println("properties: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("properties"));
	}
}
