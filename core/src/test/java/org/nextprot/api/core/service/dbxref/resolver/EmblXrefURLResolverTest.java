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

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("AF009225", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AF009225", resolver.resolve(xref));
    }

    @Test
    public void testResolveEmblWithDotAccession() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("CAH72401.1", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/CAH72401", resolver.resolve(xref));
    }
}