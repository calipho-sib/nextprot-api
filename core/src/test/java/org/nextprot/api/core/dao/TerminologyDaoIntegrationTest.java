package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class TerminologyDaoIntegrationTest extends CoreUnitBaseTest {
	
	@Autowired TerminologyDao terminologyDao;
	
	@Test
	public void shouldTheTerminologiesByInSyncWithDB() {
		
		List<String> terms = terminologyDao.findTerminologyNamesList();

		assertEquals(terms.size(), TerminologyCv.values().length);
		
		for(TerminologyCv t : TerminologyCv.values()){
			if (! t.equals(TerminologyCv.NextprotCellosaurusCv)) { // TEMP pass thru
				if(!terms.contains(t.name())){
					fail(t + " is not contained anymore");
				}
			}
		}
		
	}

}
