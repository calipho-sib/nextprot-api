package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class ConstantXrefDatabasebURLResolverTest {

    @Test
    public void testResolvePrositeProRuleDbURL() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantXrefDatabasebURLResolver("http://prosite.expasy.org/");

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PRU00156", "PROSITE-ProRule", "https://prosite.expasy.org/");

        Assert.assertEquals("http://prosite.expasy.org/", resolver.getValidXrefURL(xref.getUrl(), "PROSITE-ProRule"));
    }
}