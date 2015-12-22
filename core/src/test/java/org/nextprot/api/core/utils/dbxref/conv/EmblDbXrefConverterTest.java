package org.nextprot.api.core.utils.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

import java.util.Collections;
import java.util.List;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;
import static org.nextprot.api.core.utils.dbxref.conv.DbXrefConverterTest.createDbXrefProperty;

public class EmblDbXrefConverterTest {

    @Test
    public void testConvertEx1() {

        DbXrefPropertyToXrefConverter converter = new EmblDbXrefConverter();

        DbXref xref = createDbXref("AAA59172.1", XRefDatabase.EMBL.getName(), "http://www.ebi.ac.uk/ena/data/view/%s");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(1724225, "genomic sequence ID", "J00265")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 1724225, ref.getDbXrefId().longValue());
        Assert.assertEquals("J00265", ref.getAccession());
        Assert.assertTrue(ref.getProperties().isEmpty());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertEquals(XRefDatabase.EMBL.getName(), ref.getDatabaseName());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", ref.getLinkUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/J00265", ref.getResolvedUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/refseq/", ref.getUrl());
    }

    @Test
    public void testConvertEx2() {

        DbXrefPropertyToXrefConverter converter = new EmblDbXrefConverter();

        DbXref xref = createDbXref("AY899304", XRefDatabase.EMBL.getName(), "http://www.ebi.ac.uk/ena/data/view/%s");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(1724230, "protein sequence ID", "AAW83741.1")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 1724230, ref.getDbXrefId().longValue());
        Assert.assertEquals("AAW83741.1", ref.getAccession());
        Assert.assertTrue(ref.getProperties().isEmpty());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertEquals(XRefDatabase.EMBL.getName(), ref.getDatabaseName());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", ref.getLinkUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AAW83741", ref.getResolvedUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/refseq/", ref.getUrl());
    }
}