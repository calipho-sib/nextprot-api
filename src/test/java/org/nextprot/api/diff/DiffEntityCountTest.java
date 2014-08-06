package org.nextprot.api.diff;

import org.junit.Test;


public class DiffEntityCountTest extends DiffBaseTest {
	
	@Test
	public void testMasterCount(){
		diffCount("count-valid-master");
	}
	@Test
	public void testIsoformCount(){
		diffCount("count-valid-isoform");
	}
}
