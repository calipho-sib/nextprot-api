package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.release.ReleaseDataSources;

public class HpaXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new HpaXrefURLResolver();
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveHPAGene() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647", "HPA", "whatever");
        System.out.println("HPA datasource base url:"+ReleaseDataSources.HPA.getUrl());
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/subcellular", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPAAntibody() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("HPA018312", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/search/HPA018312", resolver.resolve(xref));
    }
}