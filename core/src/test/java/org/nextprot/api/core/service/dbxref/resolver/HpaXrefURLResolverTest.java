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
    	// page summary:
    	// OK with redirection to https://www.proteinatlas.org/ENSG00000254647-INS
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647", "HPA", "whatever");
        System.out.println("HPA datasource base url:"+ReleaseDataSources.HPA.getUrl());
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {
    	// TODO use .../cell
    	// OK with redirection to https://www.proteinatlas.org/ENSG00000254647-INS/cell 
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/subcellular", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPAAntibody() throws Exception {

    	// nice todo:                https://www.proteinatlas.org/ENSG00000172534-HCFC1/antibody
    	// if multiple antibodies:   https://www.proteinatlas.org/ENSG00000254245-PCDHGA3/antibody
    	// if multiple antibodies:   https://www.proteinatlas.org/ENSG00000085224-ATRX/antibody
    	
    	
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("HPA018312", "HPA", "whatever");
        // unexpected value is a broken URL, actual value is VALID URL ???
        // actual value: https://www.proteinatlas.org/search/HPA018312
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "search/HPA018312", resolver.resolve(xref)); 
    }
}