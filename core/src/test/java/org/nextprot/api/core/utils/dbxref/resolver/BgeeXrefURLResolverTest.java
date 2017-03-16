package org.nextprot.api.core.utils.dbxref.resolver;

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

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeENSG() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000172534", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.org/?page=gene&gene_id=ENSG00000172534", resolver.resolve(xref));
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeNoENSG() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P51610", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?uniprot_id=P51610", resolver.resolve(xref));
    }
}