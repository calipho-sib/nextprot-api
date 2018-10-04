package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@ActiveProfiles({"dev"})
public class TerminologyServiceTest extends CoreUnitBaseTest {

	@Autowired private TerminologyService terminologyService;
	
	@Test
	public void testTerminologyProperties() {
		List<CvTerm.TermProperty> properties = TerminologyUtils.convertToProperties("Sex of cell:=Female | Category:=Cancer cell line | Comment:=Part of: JFCR39 cancer cell lines panel. | Comment:=Part of: ENCODE project common cell types; tier 3. | Comment:=Part of: Genscript tumor cell line panel. | Comment:=Part of: NCI60 cancer cell lines panel. | Comment:=Discontinued: ICLC; HTL97004; probable. | Comment:=Omics: deep exome analysis. | Comment:=Omics: deep proteome analysis. | Comment:=Misspelling: occasionally 'OVCA3'. | Comment:=Omics: exosome analysis by proteomics. | Comment:=Omics: secretome analysis by proteomics.",(long) 99999, "CVCL_0000");
		assertEquals(properties.size(), 12);
		assertEquals(properties.get(3).gettermId(), (long)99999);
	}

	@Test
	public void shouldReturnAUniprotKeywordId() {
		CvTerm term = this.terminologyService.findCvTermByAccession("KW-0732");
		assertEquals("UniprotKeywordCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotSubcell() {
		CvTerm term = this.terminologyService.findCvTermByAccession("SL-0276");
		assertEquals("UniprotSubcellularLocationCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotDomain() {
		CvTerm term = this.terminologyService.findCvTermByAccession("DO-00031");
	assertEquals("NextprotDomainCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAGOTerm() {
		CvTerm term = this.terminologyService.findCvTermByAccession("GO:2000145");
		//System.out.println(term.toString());
		assertEquals("GoBiologicalProcessCv", term.getOntology());
		assertEquals(2, term.getSynonyms().size());
	}
	
	@Test
	public void shouldReturnACellosaurusTerm() {
		CvTerm term = this.terminologyService.findCvTermByAccession("CVCL_J530");
		//System.out.println(term.toString());
		assertEquals("NextprotCellosaurusCv", term.getOntology());
		assertEquals(2, term.getXrefs().size());
	}
	
	@Test
	public void shouldReturnTheHierarchy() {
		CvTerm term = this.terminologyService.findCvTermByAccession("KW-0906");
		assertEquals(3, term.getAncestorAccession().size()); // Nuclear pore complex has 3 parents
	}

	@Test
	public void shouldReturnAValidCategory() {
		CvTerm term = this.terminologyService.findCvTermByAccession("DO-00861");
		String propval = "";
		for (CvTerm.TermProperty property : term.getProperties()) {
			if(property.getPropertyName().equals("Feature category")) propval=property.getPropertyValue(); 
		}
		assertEquals("zinc finger", propval); 
	}

	@Test
	public void shouldReturnUniprotFamilies() {
		List<CvTerm> terms = this.terminologyService.findCvTermsByOntology("UniprotFamilyCv");
		assertTrue(terms.size() > 9700);
	}

	@Test
	public void shouldReturnAllTerms()  {
		int numberOfTermsHavingRelatedTerms = 0, sizeOfRelatedTerms = 0, maxSizeOfRelatedTerms = 0; 
		String tac=null;
		List<CvTerm> terms = this.terminologyService.findAllCVTerms();
		assertTrue(terms.size() > 145000);
		for(CvTerm term : terms)  {
			List<String> relatedAcs = term.getACsOfRelatedTerms();
			if(relatedAcs != null) {
				numberOfTermsHavingRelatedTerms++;
				sizeOfRelatedTerms = relatedAcs.size();
				if(sizeOfRelatedTerms > maxSizeOfRelatedTerms) {
					maxSizeOfRelatedTerms = sizeOfRelatedTerms;
					tac = term.getAccession();
				}
			}
		}
		assertTrue(numberOfTermsHavingRelatedTerms > 44000);
		assertTrue(maxSizeOfRelatedTerms > 4000 && maxSizeOfRelatedTerms < 6000);
	}

	
	@Ignore
	@Test
	public void computeRelatedTermDistribution()  {

		int[] xaxis = new int[20];
		List<CvTerm> terms = this.terminologyService.findAllCVTerms();
		for(CvTerm term : terms)  {
			List<String> relatedAcs = term.getACsOfRelatedTerms();
			if (relatedAcs== null || relatedAcs.isEmpty()) {
				xaxis[0] = xaxis[0] + 1;
			} else {
				int index = 32 - Integer.numberOfLeadingZeros(relatedAcs.size());
				xaxis[index] = xaxis[index] +1; 
			}
		}
		for (int i=0;i<20;i++) {
			System.out.println(i + "\t" + xaxis[i]);
		}
	}

	
	@Test
	public void shouldFindXrefPsiMod()  {

		List<String> accessionList = terminologyService.findCvTermXrefAccessionList("PTM-0135", "PSI-MOD");
		Assert.assertEquals(1, accessionList.size());
		Assert.assertEquals("MOD:00134", accessionList.get(0));
	}

	@Test
	public void shouldFindXrefPsiMod2()  {

		Optional<String> accession = terminologyService.findPsiModAccession("PTM-0135");

		Assert.assertTrue(accession.isPresent());
		Assert.assertEquals("MOD:00134", accession.get());
	}

	@Test
	public void shouldFindXrefPsiModName()  {

		Optional<String> name = terminologyService.findPsiModName("PTM-0135");

		Assert.assertTrue(name.isPresent());
		Assert.assertEquals("N6-glycyl-L-lysine", name.get());
	}

	@Test
	public void shouldNotFindXrefPsiMod()  {

		List<String> accessionList = terminologyService.findCvTermXrefAccessionList("TS-0001", "PSI-MOD");

		Assert.assertTrue(accessionList.isEmpty());
	}

	@Test
	public void shouldFindXrefMesh()  {

		List<String> accessionList = terminologyService.findCvTermXrefAccessionList("TS-0001", "MeSH");

		Assert.assertEquals(1, accessionList.size());
		Assert.assertEquals("D000005", accessionList.get(0));
	}

	@Test
	public void shouldFindMultipleXrefAccessions()  {

		List<String> accessionList = terminologyService.findCvTermXrefAccessionList("TS-0079", "BRENDA");

		Assert.assertEquals(2, accessionList.size());
		Assert.assertTrue(accessionList.contains("BTO:0000553"));
		Assert.assertTrue(accessionList.contains("BTO:0000089"));
	}

	@Test
	public void shouldFindMOD00077CorrectName()  {

		Optional<String> psiModAccession = terminologyService.findPsiModAccession("PTM-0066");
		Assert.assertTrue(psiModAccession.isPresent());
		Assert.assertEquals("MOD:00077", psiModAccession.get());

		Optional<String> name = terminologyService.findPsiModName("PTM-0066");

		Assert.assertTrue(name.isPresent());
		Assert.assertEquals("asymmetric dimethyl-L-arginine", name.get());
	}

	//@Test
	public void searchCvtermWithUndefinedXrefs()  {

		Map<String, List<String>> map = new HashMap<>();

		//for (CvTerm cvTerm : terminologyService.findAllCVTerms()) {
		for (CvTerm cvTerm : terminologyService.findCvTermsByOntology("UniprotPtmCv")) {

			if (cvTerm.getXrefs() == null || cvTerm.getXrefs().isEmpty()) {

				if (!map.containsKey(cvTerm.getOntology())) {
					map.put(cvTerm.getOntology(), new ArrayList<>());
				}
				map.get(cvTerm.getOntology()).add(cvTerm.getAccession());
				//System.err.println(cvTerm.getAccession()+"\t"+cvTerm.getOntology());
			}
		}

		Collections.sort(map.get("UniprotPtmCv"));

		System.out.println(map.get("UniprotPtmCv"));
	}

    @Test
    public void testIsHierarchical() {

        EnumSet<TerminologyCv> nonHierarchicalSet = EnumSet.of(
                TerminologyCv.NextprotDomainCv,
                TerminologyCv.NextprotMetalCv,
                TerminologyCv.NextprotProteinPropertyCv,
                TerminologyCv.NextprotTopologyCv,
                TerminologyCv.NonStandardAminoAcidCv,
                TerminologyCv.OmimCv,
                TerminologyCv.OrganelleCv,
                TerminologyCv.SequenceOntologyCv,
                TerminologyCv.UniprotDiseaseCv,
                TerminologyCv.UniprotPtmCv,
                TerminologyCv.UniprotSubcellularOrientationCv
        );

        for (TerminologyCv terminologyCv : TerminologyCv.values()) {

            boolean isHierarchical = terminologyCv.isHierarchical();

            if (nonHierarchicalSet.contains(terminologyCv)) {

                Assert.assertFalse(terminologyCv + " should not be hierarchical",isHierarchical);
            }
            else {
                Assert.assertTrue(terminologyCv + " should be hierarchical", isHierarchical);
            }
        }
    }
}

