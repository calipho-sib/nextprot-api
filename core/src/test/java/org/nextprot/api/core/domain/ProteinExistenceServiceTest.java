package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.ProteinExistenceInferenceService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


// Should mock deps and unit test this service
@ActiveProfiles({ "dev","cache"})
public class ProteinExistenceServiceTest extends CoreUnitBaseTest {
        
    @Autowired
	private ProteinExistenceInferenceService proteinExistenceInferenceService;

	@Test
	public void shouldMatchRule1AlreadyPE1() {

		Assert.assertTrue(proteinExistenceInferenceService.cannotBePromotedAccordingToRule1("NX_Q13740"));
	}

	@Test
	public void shouldMatchRule1BecausePE5() {

		Assert.assertTrue(proteinExistenceInferenceService.cannotBePromotedAccordingToRule1("NX_P0C2Y1"));
	}

	@Test
	public void shouldNotMatchRule1BecausePE2() {

		Assert.assertFalse(proteinExistenceInferenceService.cannotBePromotedAccordingToRule1("NX_P0CK97"));
	}

	// TODO: see with pam
	@Ignore
	@Test
	public void shouldMatchRule2() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule2("NX_Q9NV72"));
	}

	@Test
	public void shouldNotMatchRule2OnlyOnePeptideProteotypic() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule2("NX_P69849"));
	}

    @Test
    public void shouldMatchRule3() {

        Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule3("NX_Q6SJ96"));
    }

	@Test
	public void shouldNotMatchRule3BecauseEvidenceSilver() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule3("NX_Q56UN5"));
	}

	@Test
	public void shouldMatchRule4() {

		Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule4("NX_P59646"));
	}

	@Test
	public void shouldNotMatchRule4BecausePE1() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule4("NX_Q9H8Y5"));
	}

	@Test
	public void shouldNotMatchRule4BecauseNotDetected() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule4("NX_Q9HB31"));
	}

	@Test
	public void shouldMatchRule5() {

		Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule5("NX_P12830"));
	}

	@Test
	public void shouldNotMatchRule5BecauseAssignedByUniprot() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule5("NX_Q01094"));
	}

	@Test
	public void shouldNotMatchRule6Because() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule6("NX_Q9Y6N5"));
	}

    @Test
    public void shouldMatchRule7() {

        Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule7("NX_Q9Y6N5"));
    }

	@Test
	public void shouldInferFromRule1() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q13740");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
		Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_01, pe.getRule());
	}

	// TODO: find an entry that make a correct inference
	@Ignore
	@Test
	public void shouldInferFromRule2() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q3LI61");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
		Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_02, pe.getRule());
	}

	@Test
	public void shouldInferFromRule3() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q6SJ96");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
	}

	@Test
	public void shouldInferFromRule4() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_P59646");

		Assert.assertEquals(ProteinExistence.TRANSCRIPT_LEVEL, pe.getProteinExistence());
		Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_04, pe.getRule());
	}

	@Test
	public void shouldInferFromRule5() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q6V0L0");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
	}

	// Could not find an entry that match this rule:
	// the following query was returning NX_Q9ULZ1 but infortunately SP_PER_05 inferred the promotion
	//SELECT  * WHERE {
	//  ?entry :existence /:level 2 .
	//  ?entry :isoform ?iso .
	//  ?iso :binaryInteraction ?a .
	//  ?a :evidence ?e.
	//  ?e :assignedBy source:NextProt
	//}
	@Ignore
	@Test
	public void shouldInferFromRule6() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q9ULZ1");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
		Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_06, pe.getRule());
	}

	@Test
	public void shouldNotFindInferenceRule() {

		Assert.assertFalse(proteinExistenceInferenceService.inferProteinExistence("NX_A0A087WXM9").isInferenceFound());
	}
}