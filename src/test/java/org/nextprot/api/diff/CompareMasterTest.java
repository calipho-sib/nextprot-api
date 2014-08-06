package org.nextprot.api.diff;

import org.junit.Test;


public class CompareMasterTest extends DiffBaseTest {
	
	@Test
	public void testMasterCount(){
		diffCount("count-valid-master");
	}
}
