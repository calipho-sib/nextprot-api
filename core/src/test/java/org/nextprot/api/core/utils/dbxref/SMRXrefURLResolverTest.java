package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;


public class SMRXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new SMRXrefURLResolver();
    }
    
    @Test
    public void testResolve() throws Exception {

        DbXref xref = createDbXref("A0A5B9", "SMR", "whatever");
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", resolver.resolve(xref));
    }

    @Test
    public void testResolve2() throws Exception {

        DbXref xref = createDbXref("A0A5B9", "SMR", "whatever");
        String url = DbXrefURLResolver.getInstance().resolve(xref);
        Assert.assertEquals("https://swissmodel.expasy.org/repository/uniprot/A0A5B9", url);
    }


    
    
}