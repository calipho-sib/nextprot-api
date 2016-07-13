package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class RelativeUrlUtilsTest {

	@Test
	public void testUrl() throws Exception {
		String url;
		
		url= "toto/";
		Assert.assertEquals("toto",RelativeUrlUtils.getPathElements(url)[0]);
		Assert.assertEquals(0,RelativeUrlUtils.getParamsElements(url).length);

		url= "/toto/";
		Assert.assertEquals("toto",RelativeUrlUtils.getPathElements(url)[0]);
		Assert.assertEquals(0,RelativeUrlUtils.getParamsElements(url).length);

		url= "toto/tutu?titi=happy";
		Assert.assertEquals("toto",RelativeUrlUtils.getPathElements(url)[0]);
		Assert.assertEquals("tutu",RelativeUrlUtils.getPathElements(url)[1]);
		Assert.assertEquals(1,RelativeUrlUtils.getParamsElements(url).length);
		
		url= "toto?first=joe&second=averell";
		Assert.assertEquals("toto",RelativeUrlUtils.getPathElements(url)[0]);
		Assert.assertEquals(2,RelativeUrlUtils.getParamsElements(url).length);
		Assert.assertEquals("first=joe",RelativeUrlUtils.getParamsElements(url)[0]);
		Assert.assertEquals("second=averell",RelativeUrlUtils.getParamsElements(url)[1]);
		
		url= "toto?";
		Assert.assertEquals("",RelativeUrlUtils.getParamsElements(url)[0]);
	}

	
}
