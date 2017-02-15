package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;


public class SMRXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new SMRXrefURLResolver();
    }
    
    @Test
    public void testResolve() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("A0A5B9", "SMR", "whatever");
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", resolver.resolve(xref));
    }

    @Test
    public void testResolve2() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("A0A5B9", "SMR", "whatever");
        String url = new DbXrefURLResolverDelegate().resolve(xref);
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", url);
    }


    
    
}