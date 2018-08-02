package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class StatementXRefIdTest {

    @Test
    public void shouldGetASizeOf19And125ForGlyconnect() {
        long xrefid = new StatementXRefId(125, "560/sites/51").id();
        String stringid = Long.toString(xrefid);
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("7125"));
        Assert.assertTrue(StatementXRefId.isStatementXrefId(xrefid));
        Assert.assertEquals(125L, StatementXRefId.calcXrefDatabaseId(xrefid));
        Assert.assertEquals(183862133206533L, StatementXRefId.calcTruncatedXrefAccessionId(xrefid));
        Assert.assertEquals("a738bd232e05", Long.toHexString(StatementXRefId.calcTruncatedXrefAccessionId(xrefid)));
    }

    @Test
    public void shouldGetSameSizeOf19And125ForGlyconnectWithLeadingWhitespaceAccession() {
        long xrefid = new StatementXRefId(125, " 560/sites/51").id();
        String stringid = Long.toString(xrefid);
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("7125"));
        Assert.assertTrue(StatementXRefId.isStatementXrefId(xrefid));
        Assert.assertEquals(125L, StatementXRefId.calcXrefDatabaseId(xrefid));
        Assert.assertEquals(183862133206533L, StatementXRefId.calcTruncatedXrefAccessionId(xrefid));
        Assert.assertEquals("a738bd232e05", Long.toHexString(StatementXRefId.calcTruncatedXrefAccessionId(xrefid)));
    }

    @Test
    public void shouldAlwaysStartWith7123ForAnyRandomStringFromDB123() {

        for(int i=0; i<20; i++){
            String s = UUID.randomUUID().toString();
            //long xrefid = new StatementXRefId("BioEditor", 123, s).id();
            long xrefid = new StatementXRefId(123, s).id();
            String stringid = Long.toString(xrefid);
            Assert.assertEquals(stringid.length(), 19);
            Assert.assertTrue(stringid.startsWith("7123"));
            Assert.assertTrue(StatementXRefId.isStatementXrefId(xrefid));
            Assert.assertEquals(123L, StatementXRefId.calcXrefDatabaseId(xrefid));
        }
    }
}