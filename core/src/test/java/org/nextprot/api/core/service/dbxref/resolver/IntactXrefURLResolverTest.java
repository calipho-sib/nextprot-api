package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class IntactXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new IntactXrefURLResolver();
    }

    @Test
    public void testResolveIntAct() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("EBI-1644164,EBI-396176", "IntAct", "whatever");

        Assert.assertEquals("https://www.ebi.ac.uk/intact/pages/details/details.xhtml?binary=EBI-1644164,EBI-396176", resolver.resolve(xref));
    }

    @Test
    public void testResolveIntActNoEBIId() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P51610", "IntAct", "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=%s");

        Assert.assertEquals("http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=P51610", resolver.resolve(xref));
    }
}