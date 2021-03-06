package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;


public class EnsemblXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new EnsemblXrefURLResolver();
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSG() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000178093", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=ENSG00000178093", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSP() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSP00000466056", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=ENSP00000466056", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENST() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=ENST00000587522", resolver.resolve(xref));
    }

    @Test
    public void testResolveEnsemblBadPrimaryId() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("EST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("https://www.ensembl.org/Multi/Search/Results?q=EST00000587522;site=ensembl", resolver.resolve(xref));
        Assert.assertEquals("https://www.ensembl.org/Multi/Search/Results?q=%s;site=ensembl", xref.getLinkUrl());
    }
}