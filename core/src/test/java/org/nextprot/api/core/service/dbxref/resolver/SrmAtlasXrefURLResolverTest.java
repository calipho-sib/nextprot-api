package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

public class SrmAtlasXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new SrmAtlasXrefURLResolver();
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolveSRMAtlas() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PAp00968082", "SRMAtlas", "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=%s;apply_action=QUERY");
        xref.setProperties(Collections.singletonList(DbXrefURLResolverDelegateTest.createDbXrefProperty("sequence", "GFFYTPK")));

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=GFFYTPK;apply_action=QUERY",resolver.resolve(xref));
    }
}