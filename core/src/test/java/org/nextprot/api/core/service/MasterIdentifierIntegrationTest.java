package org.nextprot.api.core.service;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIdentifierIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIdentifierService service;

	@Ignore
	@Test
	public void getListOfEntriesOfChromosome1() {
		
		List<String> acs = service.findUniqueNamesOfChromosome("1");
		for (String ac: acs) System.out.println(ac);
	}
}
