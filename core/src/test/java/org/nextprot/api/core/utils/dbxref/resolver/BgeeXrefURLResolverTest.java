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

    	String cleanAC ="stage_id=HsapDO:0000083&organ_id=EV:0100046&gene_id=ENSG00000124532&stage_children=on";
    	  
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref(cleanAC, "Bgee", "http://bgee.org/bgee/bgee?page=expression&action=data&%");

        Assert.assertEquals("http://bgee.org/bgee/bgee?page=expression&action=data&" + cleanAC, resolver.resolve(xref));
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeNoENSG() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P51610", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.org/bgee/bgee?uniprot_id=P51610", resolver.resolve(xref));
    }
}