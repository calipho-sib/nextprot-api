package org.nextprot.api.etl.pipeline.pump;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

// TODO: should not be integration tests
@Ignore
public class HttpStatementPumpTest {

	@Test(expected = IllegalArgumentException.class)
	public void constrPumpShouldThrowExceptionWithEmptyUrl() {

		new HttpStatementPump("");
	}

	@Test
	public void activatedPumpShouldBeEmpty() {

		HttpStatementPump pump = new HttpStatementPump("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");

		Assert.assertTrue(pump.isSourceEmpty());
	}

	@Test
	public void pumpShouldBeEmpty2() {

		HttpStatementPump pump = new HttpStatementPump("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");

		pump.pump();
		Assert.assertTrue(pump.isSourceEmpty());
	}
}