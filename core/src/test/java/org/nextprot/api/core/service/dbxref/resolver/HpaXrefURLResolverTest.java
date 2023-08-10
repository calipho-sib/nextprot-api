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
    	// towards page summary
    	// OK with redirection to https://www.proteinatlas.org/ENSG00000254647-INS
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647", "HPA", "whatever");
        //System.out.println(ReleaseDataSources.HPA.getUrl());
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/cell", resolver.resolve(xref));
    }

    @Test
    public void testResolveHPACell() throws Exception {
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/cell", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/cell", resolver.resolve(xref));
    }

    @Test
    public void testResolveHPAExpr() throws Exception {
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/tissue/lung", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/tissue/lung", resolver.resolve(xref));
    }

    @Test
    public void testResolveHPABloodRnaSeq() throws Exception {
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/blood/t-cells#hpa_memory_cd4_t-cell", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/blood/t-cells#hpa_memory_cd4_t-cell", resolver.resolve(xref));
    }

    @Test
    public void testResolveHPATissueExprInfo() throws Exception {
        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000254647/tissue", "HPA", "whatever");
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "ENSG00000254647/tissue", resolver.resolve(xref));
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
        // used in antibody mapping evidences
        Assert.assertEquals(ReleaseDataSources.HPA.getUrl() + "search/HPA018312", resolver.resolve(xref)); 
    }
}