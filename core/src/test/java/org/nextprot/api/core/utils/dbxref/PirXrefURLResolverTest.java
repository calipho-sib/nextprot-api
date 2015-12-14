package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;
import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXrefProperty;


public class PirXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new PirXrefURLResolver();
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIR() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIRWithoutURLProtocol() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolvePIRShouldNotThrowsNPE() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }
}