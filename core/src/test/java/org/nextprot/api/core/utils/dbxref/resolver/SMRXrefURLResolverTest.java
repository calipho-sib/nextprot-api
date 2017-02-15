package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest;


public class SMRXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new SMRXrefURLResolver();
    }
    
    @Test
    public void testResolve() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("A0A5B9", "SMR", "whatever");
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", resolver.resolve(xref));
    }

    @Test
    public void testResolve2() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("A0A5B9", "SMR", "whatever");
        String url = DbXrefURLResolver.getInstance().resolve(xref);
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", url);
    }


    
    
}