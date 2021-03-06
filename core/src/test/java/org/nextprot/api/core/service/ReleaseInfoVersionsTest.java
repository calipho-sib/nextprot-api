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
	public void shouldFindDataSources_After_Jul_06() {

		if (todayIsAfter("06 Jul 2020")) {
			// we assume that at this date the db declared in application-dev.properties
			// will contain the MassIVe datasource
			int datasourcesSize = releaseInfoService.findReleaseDatasources().getDatasources().size();
			assertEquals( ReleaseDataSources.values().length,  datasourcesSize);
		}
	}

}
