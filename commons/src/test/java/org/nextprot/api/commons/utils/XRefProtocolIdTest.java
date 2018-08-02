package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class XRefProtocolIdTest {

    @Test
    public void shouldGetASizeOf19And125ForGlyconnect() {
        long xrefid = new XRefProtocolId(125, "560/sites/51").id();
        String stringid = Long.toString(xrefid);
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("7125"));
        Assert.assertTrue(XRefProtocolId.isXrefProtocolId(xrefid));
        Assert.assertEquals(125L, XRefProtocolId.calcXrefDatabaseId(xrefid));
        Assert.assertEquals(183862133206533L, XRefProtocolId.calcTruncatedXrefAccessionId(xrefid));
        Assert.assertEquals("a738bd232e05", Long.toHexString(XRefProtocolId.calcTruncatedXrefAccessionId(xrefid)));
    }

    @Test
    public void shouldGetSameSizeOf19And125ForGlyconnectWithLeadingWhitespaceAccession() {
        long xrefid = new XRefProtocolId(125, " 560/sites/51").id();
        String stringid = Long.toString(xrefid);
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("7125"));
        Assert.assertTrue(XRefProtocolId.isXrefProtocolId(xrefid));
        Assert.assertEquals(125L, XRefProtocolId.calcXrefDatabaseId(xrefid));
        Assert.assertEquals(183862133206533L, XRefProtocolId.calcTruncatedXrefAccessionId(xrefid));
        Assert.assertEquals("a738bd232e05", Long.toHexString(XRefProtocolId.calcTruncatedXrefAccessionId(xrefid)));
    }

    @Test
    public void shouldAlwaysStartWith7123ForAnyRandomStringFromDB123() {

        for(int i=0; i<20; i++){
            String s = UUID.randomUUID().toString();
            //long xrefid = new StatementXRefId("BioEditor", 123, s).id();
            long xrefid = new XRefProtocolId(123, s).id();
            String stringid = Long.toString(xrefid);
            Assert.assertEquals(stringid.length(), 19);
            Assert.assertTrue(stringid.startsWith("7123"));
            Assert.assertTrue(XRefProtocolId.isXrefProtocolId(xrefid));
            Assert.assertEquals(123L, XRefProtocolId.calcXrefDatabaseId(xrefid));
        }
    }
}