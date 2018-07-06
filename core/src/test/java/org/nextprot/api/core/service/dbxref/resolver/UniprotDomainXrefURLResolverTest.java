package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class UniprotDomainXrefURLResolverTest {

    @Test
    public void testResolveRepeatTypeDomain() {

        UniprotDomainXrefURLResolver resolver = new UniprotDomainXrefURLResolver();

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("Kelch repeat", "UniProt domain", "");

        Assert.assertEquals("https://www.uniprot.org/uniprot/?query=annotation%3A%28type%3Apositional+Kelch%29", resolver.resolve(xref));
    }

    @Test
    public void testResolveDomainTypeDomain() {

        UniprotDomainXrefURLResolver resolver = new UniprotDomainXrefURLResolver();

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("KBD domain", "UniProt domain", "");

        Assert.assertEquals("https://www.uniprot.org/uniprot/?query=annotation%3A%28type%3Apositional+KBD%29", resolver.resolve(xref));
    }

    @Test
    public void testResolveZincFingerTypeDomain() {

        UniprotDomainXrefURLResolver resolver = new UniprotDomainXrefURLResolver();

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("KBD zinc finger", "UniProt domain", "");

        Assert.assertEquals("https://www.uniprot.org/uniprot/?query=annotation%3A%28type%3Apositional+KBD%29", resolver.resolve(xref));
    }
}