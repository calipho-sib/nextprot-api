package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;
import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXrefProperty;

public class HsspXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new HsspXrefURLResolver();
    }

    @Test
    public void testResolveHSSP() throws Exception {

        DbXref xref = createDbXref("PS50853", "HSSP", "%s");

        Assert.assertEquals("http://ps50853", resolver.resolve(xref));
    }

    @Test
    public void testResolveHSSPWithPDB() throws Exception {

        DbXref xref = createDbXref("PS50853", "HSSP", "%s");
        xref.setProperties(Collections.singletonList(createDbXrefProperty("PDB accession", "1A7F")));

        Assert.assertEquals("http://1a7f", resolver.resolve(xref));
    }
}