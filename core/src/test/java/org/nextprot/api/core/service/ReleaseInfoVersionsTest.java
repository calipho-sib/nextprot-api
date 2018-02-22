package org.nextprot.api.core.service;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseDataSources;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author pmichel
 * These tests require computation on the full set of entries  
 * => 4 hours at least with cache
 * => untractable without cache
 * And these tests don't test anything relevant
 * => ignored
 */

@ActiveProfiles({ "dev" })
public class ReleaseInfoVersionsTest extends CoreUnitBaseTest {
	
	@Autowired private ReleaseInfoService releaseInfoService;

	@Test
	public void shouldFindAValdiDatabaseRelease() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dbRelease = releaseInfoService.findReleaseVersions().getDatabaseRelease();
			Date d = null;
			d = dateFormat.parse(dbRelease);
			assertTrue(d != null);
		} catch (ParseException e) {
			fail();
		}
	}
	
	
	@Test
	public void shouldFindDataSources() {
		int datasourcesSize = releaseInfoService.findReleaseDatasources().getDatasources().size();
		assertEquals(ReleaseDataSources.values().length,  datasourcesSize);
	}

}
