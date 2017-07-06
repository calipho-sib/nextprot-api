package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@ActiveProfiles({ "dev" })
public class TerminologyDaoIntegrationTest extends CoreUnitBaseTest {
	
	@Autowired TerminologyDao terminologyDao;
	
	@Test
	public void shouldTheTerminologiesByInSyncWithDB() {
		
		List<String> terminologies = terminologyDao.findTerminologyNamesList();
		
		List<TerminologyCv> tCv = 
				Arrays.stream(TerminologyCv.values())
				.collect(Collectors.toList());

		assertEquals(terminologies.size(), tCv.size());
		
		for(TerminologyCv t : tCv){
			if (! t.equals(TerminologyCv.NextprotCellosaurusCv)) { // TEMP pass thru
				if(!terminologies.contains(t.name())){
					fail(t + " is not contained anymore");
				}
			}
		}
		
	}

}
