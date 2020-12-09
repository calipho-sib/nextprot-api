package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class BgeeXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new BgeeXrefURLResolver();
    }

    @Test
    public void testResolveBgeeENSG() throws Exception {

    	String cleanAC ="gene_id=ENSG00000124532";
    	  
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref(cleanAC, "Bgee", "https://bgee.org/bgee14_1/?page=gene&gene_id=%s");

        Assert.assertEquals("https://bgee.org/bgee14_1/?page=gene&gene_id=" + cleanAC, resolver.resolve(xref));
    }

}