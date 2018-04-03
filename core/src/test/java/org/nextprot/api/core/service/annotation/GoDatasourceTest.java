package org.nextprot.api.core.service.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class GoDatasourceTest extends CoreUnitBaseTest {


    @Test
    public void shouldReturnAKnownSource()  {
				
		Assert.assertEquals("GOA curators", GoDatasource.getGoAssignedBy("GO_REF:0000107"));
    }	

    @Test
    public void shouldReturnNullIfNotAKnownGoRef()  {
		
		Assert.assertNull(GoDatasource.getGoAssignedBy("tralala") );
    }	

}
