package org.nextprot.api.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class ExperimentalContextUtilsTest extends CoreUnitBaseTest {

	/*
	 * pam, 16 Oct 2020
	 * md5 is a column in the NP1 experimental_contexts table
	 * It is not clear how it is used but it is a mandatory field
	 * The computation of md5 is a copy of how it is computed in 
	 * NP1 nextprot-loaders code
    */
	

	@Test
    public void testTypicalBgeeMd5() {
        String md5 = ExperimentalContextUtil.computeMd5ForBgee("TS-0229", "HsapDO:0000197", "ECO:0000009");
        assertEquals("5e475c59db69bd1dd33b39099672a105", md5);
    }

	@Test
    public void testTypicalHPAMd5() {		
        String md5 = ExperimentalContextUtil.computeMd5ForHPA("TS-0285", "ECO:0000295");
        assertEquals("dbf2447ac049c4b335e9bcfc746bc6c4", md5);
    }

}
