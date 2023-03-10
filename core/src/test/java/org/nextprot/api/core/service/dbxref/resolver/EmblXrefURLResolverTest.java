package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class EmblXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new EmblXrefURLResolver();
    }

    @Test
    public void testResolveEmbl() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("AF009225", "EMBL", "https://www.ebi.ac.uk/ena/browser/view/%s");

        https://www.ebi.ac.uk/ena/browser/view/AF009225
        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/AF009225", resolver.resolve(xref));
    }

    @Test
    public void testResolveEmblWithDotAccession() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("AF009225.1", "EMBL", "https://www.ebi.ac.uk/ena/browser/view/%s");

        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/AF009225", resolver.resolve(xref));
    }
}