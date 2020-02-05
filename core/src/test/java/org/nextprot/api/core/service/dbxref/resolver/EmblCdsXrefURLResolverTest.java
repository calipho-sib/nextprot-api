package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class EmblCdsXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new EmblCdsXrefURLResolver();
    }


    @Test
    public void testResolveGenevisibleBadTemplate() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("CAB90389", "EMBL-CDS", "whatever");

        Assert.assertEquals("https://www.ebi.ac.uk/ena/data/view/CAB90389", resolver.resolve(xref));
    }
    
}