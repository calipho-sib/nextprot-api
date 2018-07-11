package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.utils.MultithreadedStressTester;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles({ "dev" })
public class MasterIdentifierIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIdentifierService service;

	@Autowired
    private MainNamesService mainNamesService;

	@Ignore
	@Test
	public void getListOfEntriesOfChromosome1() {

		List<String> acs = service.findUniqueNamesOfChromosome("1");
		for (String ac : acs)
			System.out.println(ac);
	}

	@Test
	public void shouldFindEntryAccessionByGeneName() {

		Set<String> accession = service.findEntryAccessionByGeneName("ins", false);
		Assert.assertEquals(accession.size(), 1);

		Assert.assertEquals(accession.iterator().next(), "NX_P01308");

	}
	
	
	@Test
	public void shouldNotFindSeveralAccessionAnymore() {

		Set<String> accession = service.findEntryAccessionByGeneName("GCNT2",false);
		System.out.println(accession);
		Assert.assertEquals(accession.size(), 1);
	}

	@Test
    public void testMainNamesMapIsValid() throws InterruptedException {

        MultithreadedStressTester stressTester = new MultithreadedStressTester(10, 1000000);

        stressTester.stress(() -> assertMainNamesMapValid());
        stressTester.shutdown();

        assertMainNamesMapValid();
    }

    private void assertMainNamesMapValid() {

        Assert.assertEquals(62471, mainNamesService.findIsoformOrEntryMainName().size());
        Assert.assertTrue(mainNamesService.findIsoformOrEntryMainName("NX_P52701").isPresent());
        Assert.assertTrue(mainNamesService.findIsoformOrEntryMainName("NX_P01308").isPresent());
        Assert.assertEquals("Insulin", mainNamesService.findIsoformOrEntryMainName("NX_P01308").get().getName());
    }
}
