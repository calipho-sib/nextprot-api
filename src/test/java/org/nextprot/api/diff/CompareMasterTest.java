package org.nextprot.api.diff;

import org.junit.Test;


public class CompareMasterTest extends CompareBaseTest {
	
	@Test
	public void testMasterCount(){
		diffCount("count-valid-master");
	}
}
