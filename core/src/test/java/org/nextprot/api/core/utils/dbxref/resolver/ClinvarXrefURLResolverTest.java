package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class ClinvarXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new ClinvarXrefURLResolver();
    }

    @Test
    public void testResolveClinvar() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("HCFC1", "Clinvar", "url.whatever");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/?term=HCFC1", resolver.resolve(xref));
    }
}