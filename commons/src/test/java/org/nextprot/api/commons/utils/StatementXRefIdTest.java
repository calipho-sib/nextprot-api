package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class StatementXRefIdTest {

    @Test
    public void shouldGetASizeOf19And125ForGlyconnect() throws Exception {
        //long xrefid = new StatementXRefId("GlyConnect", 125, "560/sites/51").id();
        long xrefid = new StatementXRefId(125, "560/sites/51").id();
        String stringid = Long.toString(xrefid);
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("701250"));
        Assert.assertTrue(StatementXRefId.isStatementXrefId(xrefid));
        Assert.assertEquals(125L, StatementXRefId.calcXrefDatabaseId(xrefid));
    }

    @Test
    public void shouldAlwaysStartWith701230ForAnyRandomString() throws Exception {

        for(int i=0; i<20; i++){
            String s = UUID.randomUUID().toString();
            //long xrefid = new StatementXRefId("BioEditor", 123, s).id();
            long xrefid = new StatementXRefId(123, s).id();
            String stringid = Long.toString(xrefid);
            Assert.assertEquals(stringid.length(), 19);
            Assert.assertTrue(stringid.startsWith("701230"));
            Assert.assertTrue(StatementXRefId.isStatementXrefId(xrefid));
            Assert.assertEquals(123L, StatementXRefId.calcXrefDatabaseId(xrefid));
        }
    }
}