package org.nextprot.api.core.utils.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverDelegateTest;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverSupplier;

import java.util.Collections;
import java.util.List;

public class DbXrefConverterTest {

    @Test
    public void testConvert() {

        DbXrefPropertyToXrefConverter converter = DbXrefConverter.getInstance();

        DbXref xref = createDbXref("NP_000198.1", DbXrefURLResolverSupplier.REF_SEQ.getXrefDatabase().getName(), "http://www.ncbi.nlm.nih.gov/protein/%s", "http://www.ncbi.nlm.nih.gov/refseq/");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(5309676, "nucleotide sequence ID", "NM_000207.2")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 5309676L, ref.getDbXrefId().longValue());
        Assert.assertEquals("NM_000207.2", ref.getAccession());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/nuccore/%s", ref.getLinkUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/refseq/", ref.getUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/nuccore/NM_000207.2", ref.getResolvedUrl());
        Assert.assertEquals(DbXrefURLResolverSupplier.REF_SEQ.getXrefDatabase(), ref.getDatabaseName());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertTrue(ref.getProperties().isEmpty());
    }

    @Test
    public void testConvertWithUnfoundConverterProduceEmptyDbXrefList() {

        DbXrefPropertyToXrefConverter converter = DbXrefConverter.getInstance();

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("NP_000198.1", DbXrefURLResolverSupplier.JCRB.getXrefDatabase().getName(), "http://www.ncbi.nlm.nih.gov/protein/%s");

        Assert.assertTrue(converter.convert(xref).isEmpty());
    }

    @Test
    public void testConvertEx1() {

        DbXrefPropertyToXrefConverter converter =  DbXrefConverter.getInstance();

        DbXref xref = createDbXref("AAA59172.1", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(), "http://www.ebi.ac.uk/ena/data/view/%s", "http://www.ebi.ac.uk/ena");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(1724225, "genomic sequence ID", "J00265")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 1724225, ref.getDbXrefId().longValue());
        Assert.assertEquals("J00265", ref.getAccession());
        Assert.assertTrue(ref.getProperties().isEmpty());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertEquals(DbXrefURLResolverSupplier.EMBL.getXrefDatabase(), ref.getDatabaseName());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", ref.getLinkUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/J00265", ref.getResolvedUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena", ref.getUrl());
    }

    public static DbXref createDbXref(String accession, String dbName, String linkURL, String url) {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref(accession, dbName, linkURL);

        xref.setUrl(url);

        return xref;
    }

    public static DbXref.DbXrefProperty createDbXrefProperty(long id, String name, String value) {

        DbXref.DbXrefProperty prop = DbXrefURLResolverDelegateTest.createDbXrefProperty(name, value);

        prop.setPropertyId(id);

        return prop;
    }
}