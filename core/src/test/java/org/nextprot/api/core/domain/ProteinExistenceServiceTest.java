package org.nextprot.api.core.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.ProteinExistenceInferenceService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


// TODO: Should mock deps and unit test this service
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
	public void shouldBeOK() {
		assertTrue(   todayIsAfter("22 Feb 1999"));
		assertTrue( ! todayIsAfter("18 Jul 2028"));
	}
	
	
	@Test
	public void shouldMatchRule4() {
		
		// if this fails after 10 March, then install nextprot and nxflat db of release 2020_02 to DEV pleatform (crick)
		
		if (todayIsAfter("10 Mar 2020")) { 
			Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule4("NX_P59646"));
		}
	}

	@Test
	public void shouldNotMatchRule4BecausePE1() {

		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule4("NX_Q9H8Y5"));
	}

	@Test
	public void shouldNotMatchRule4BecauseNotDetected() {
		// cases since release 2020_02
		// with N "not detected" and 0 "detected" in RNA-seq evidences of expression profile annotations
		// NX_Q5VXH4	0	65
		// NX_Q96LA9	0	65
		// NX_P0C7T3	0	56
		Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule4("NX_Q5VXH4"));
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

        Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule7("NX_O96004"));
    }

    @Test
    public void shouldAlsoMatchRule7() {

        Assert.assertTrue(proteinExistenceInferenceService.promotedAccordingToRule7("NX_Q9NZH5"));
    }

    @Test
    public void shouldNotMatchRule7() {

	    // unfortunately the evidence associated is not experimental
        Assert.assertFalse(proteinExistenceInferenceService.promotedAccordingToRule7("NX_Q9NP62"));
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

		// if this fails after 10 March, then install nextprot and nxflat db of release 2020_02 to DEV pleatform (crick)
		
		if (todayIsAfter("10 Mar 2020")) { 

			ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_P59646");
			Assert.assertEquals(ProteinExistence.TRANSCRIPT_LEVEL, pe.getProteinExistence());
			Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_04, pe.getRule());
		}
	}

	@Test
	public void shouldInferFromRule5() {

		ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_Q6V0L0");

		Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
	}

	
	
	
	@Test
	public void shouldInferFromRule6() {

//		Example in data release 2020_02		
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_A6NEM1 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_P0CL80 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_P0CL81 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_P0CV98 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_Q07627 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_Q6S545 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_Q6UXS9 to PE1
//		INFO: ProteinExistence: promotedAccordingToRule6: NX_Q9BXU9 to PE1
		
		// if this fails after 10 March, then install nextprot and nxflat db of release 2020_02 to DEV pleatform (crick)
		
		if (todayIsAfter("10 Mar 2020")) { 
			ProteinExistenceInferred pe = proteinExistenceInferenceService.inferProteinExistence("NX_A6NEM1");
			Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, pe.getProteinExistence());
			Assert.assertEquals(ProteinExistenceInferred.ProteinExistenceRule.SP_PER_06, pe.getRule());
		}
	}

	@Ignore
	@Test
	public void shouldNotFindInferenceRule() {

		Assert.assertFalse(proteinExistenceInferenceService.inferProteinExistence("NX_A0A087WXM9").isInferenceFound());
	}
}