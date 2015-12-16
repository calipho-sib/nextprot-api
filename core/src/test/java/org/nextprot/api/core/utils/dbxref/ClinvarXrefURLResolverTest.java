package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class ClinvarXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new ClinvarXrefURLResolver();
    }

    @Test
    public void testResolveClinvar() throws Exception {

        DbXref xref = createDbXref("HCFC1", "Clinvar", "url.whatever");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/?term=HCFC1", resolver.resolve(xref));
    }
}