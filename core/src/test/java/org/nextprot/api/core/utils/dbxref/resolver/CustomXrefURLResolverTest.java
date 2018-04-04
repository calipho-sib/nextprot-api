package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class CustomXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new DefaultDbXrefURLResolver();
    }

    @Test
    public void testResolveNextProtTerm() throws Exception {
    	  
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("DI-03265", "NextProt", "http://www.nextprot.org/term/%s");

        Assert.assertEquals("http://www.nextprot.org/term/DI-03265" , resolver.resolve(xref));
    }

}