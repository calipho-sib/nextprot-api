package org.nextprot.api.core.service.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverSupplier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.conv.DbXrefConverterTest.createDbXref;
import static org.nextprot.api.core.service.dbxref.conv.DbXrefConverterTest.createDbXrefProperty;

public class EmblDbXrefConverterTest {

    @Test
    public void testConvertEx1() {

        EmblDbXrefConverter converter = new EmblDbXrefConverter();

        DbXref xref = createDbXref("AAA59172.1", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(), "https://www.ebi.ac.uk/ena/browser/view/%s", "http://www.ebi.ac.uk/ena");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(1724225, "genomic sequence ID", "J00265")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 1724225, ref.getDbXrefId().longValue());
        Assert.assertEquals("J00265", ref.getAccession());
        Assert.assertTrue(ref.getProperties().isEmpty());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertEquals(XrefDatabase.EMBL.getName(), ref.getDatabaseName());
        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/%s", ref.getLinkUrl());
        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/J00265", ref.getResolvedUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena", ref.getUrl());
    }

    @Test
    public void testConvertEx2() {

        EmblDbXrefConverter converter = new EmblDbXrefConverter();

        DbXref xref = createDbXref("AY899304", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(), "https://www.ebi.ac.uk/ena/browser/view/%s", "http://www.ebi.ac.uk/ena");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(1724230, "protein sequence ID", "AAW83741.1")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 1724230, ref.getDbXrefId().longValue());
        Assert.assertEquals("AAW83741.1", ref.getAccession());
        Assert.assertTrue(ref.getProperties().isEmpty());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertEquals(XrefDatabase.EMBL.getName(), ref.getDatabaseName());
        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/%s", ref.getLinkUrl());
        Assert.assertEquals("https://www.ebi.ac.uk/ena/browser/view/AAW83741", ref.getResolvedUrl());
        Assert.assertEquals("http://www.ebi.ac.uk/ena", ref.getUrl());
    }

    @Test
    public void testConvertFromMultipleProps() {

        EmblDbXrefConverter converter = new EmblDbXrefConverter();

        DbXref xref = createDbXref("AY899304", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(), "https://www.ebi.ac.uk/ena/browser/view/%s", "http://www.ebi.ac.uk/ena");
        xref.setProperties(Arrays.asList(
                createDbXrefProperty(1935564, "genomic sequence ID", "M27429"),
                createDbXrefProperty(1935565, "genomic sequence ID", "M27428"),
                createDbXrefProperty(1935566, "genomic sequence ID", "M27427"),
                createDbXrefProperty(1935567, "genomic sequence ID", "M27426"),
                createDbXrefProperty(1935568, "genomic sequence ID", "M27425"),
                createDbXrefProperty(1935569, "genomic sequence ID", "M27424"),
                createDbXrefProperty(1935570, "genomic sequence ID", "M27423"),
                createDbXrefProperty(1935571, "molecule type", "protein"),
                createDbXrefProperty(1935572, "genomic sequence ID", "M27430")
        ));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(8, xrefs.size());

        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935564, "M27429", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27429");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935565, "M27428", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27428");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935566, "M27427", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27427");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935567, "M27426", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27426");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935568, "M27425", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27425");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935569, "M27424", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27424");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935570, "M27423", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27423");
        assertProducedXrefListContains(xrefs, IdentifierOffset.XREF_PROPERTY_OFFSET + 1935572, "M27430", DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName(),
                "Sequence databases", "https://www.ebi.ac.uk/ena", "https://www.ebi.ac.uk/ena/browser/view/%s", "https://www.ebi.ac.uk/ena/browser/view/M27430");
    }

    private void assertProducedXrefListContains(List<DbXref> producedXrefs, long expectedId, String expectedAccession, String expectedDbName,
                              String expectedDbCategory, String expectedUrl, String expectedLinkUrl, String expectedResolvedUrl) {

        DbXref xrefToCheck = null;

        for (DbXref producedXref : producedXrefs) {

            if (producedXref.getDbXrefId() == expectedId) {

                xrefToCheck = producedXref;
                break;
            }
        }

        Assert.assertNotNull(xrefToCheck);

        Assert.assertEquals(expectedId, xrefToCheck.getDbXrefId().longValue());
        Assert.assertEquals(expectedAccession, xrefToCheck.getAccession());
        Assert.assertTrue(xrefToCheck.getProperties().isEmpty());
        Assert.assertEquals(expectedDbName, xrefToCheck.getDatabaseName());
        Assert.assertEquals(expectedDbCategory, xrefToCheck.getDatabaseCategory());
        Assert.assertEquals(expectedUrl, xrefToCheck.getUrl());
        Assert.assertEquals(expectedLinkUrl, xrefToCheck.getLinkUrl());
        Assert.assertEquals(expectedResolvedUrl, xrefToCheck.getResolvedUrl());
    }
}