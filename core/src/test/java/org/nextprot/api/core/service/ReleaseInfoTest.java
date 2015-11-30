package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseDataSources;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class ReleaseInfoTest extends CoreUnitBaseTest {
	
	@Autowired private ReleaseInfoService releaseInfoService;

	@Test
	public void shouldFindAValdiDatabaseRelease() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dbRelease = releaseInfoService.findReleaseInfo().getDatabaseRelease();
			Date d = null;
			d = dateFormat.parse(dbRelease);
			assertTrue(d != null);
		} catch (ParseException e) {
			fail();
		}
	}
	
	
	@Test
	public void shouldFindDataSources() {
		int datasourcesSize = releaseInfoService.findReleaseInfo().getDatasources().size();
		assertEquals(ReleaseDataSources.values().length,  datasourcesSize);
	}

}
