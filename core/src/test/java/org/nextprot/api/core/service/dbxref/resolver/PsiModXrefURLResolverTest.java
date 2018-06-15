package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

public class PsiModXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new PsiModXrefURLResolver();
    }

    @Test
    public void testResolvePsiMod() {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("MOD:00134", XrefDatabase.PSIMOD.getName(),
                "http://www.ebi.ac.uk/ontology-lookup/?termId=MOD:%s");

        Assert.assertEquals("https://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http://purl.obolibrary.org/obo/MOD_00134", resolver.resolve(xref));
    }
}
