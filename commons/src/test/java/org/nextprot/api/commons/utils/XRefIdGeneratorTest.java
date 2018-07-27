package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XRefIdGeneratorTest {


    private Map<String, Integer> dictionary = new HashMap<String, Integer>()
    {{
        put("BioEditor", 123);
        put("GlyConnect", 125);
    }};


    @Test
    public void shouldGetASizeOf19And125ForGlyconnect() throws Exception {
        long xrefid = new XRefIdGenerator("GlyConnect", "560/sites/51").build(dictionary);
        String stringid = new Long(xrefid).toString();
        Assert.assertEquals(stringid.length(), 19);
        Assert.assertTrue(stringid.startsWith("701250"));
    }


    @Test
    public void shouldAlwaysStartWith701230ForAnyRandomString() throws Exception {

        for(int i=0; i<20; i++){
            String s = UUID.randomUUID().toString();
            long xrefid = new XRefIdGenerator("BioEditor", s).build(dictionary);
            String stringid = new Long(xrefid).toString();
            Assert.assertEquals(stringid.length(), 19);
            Assert.assertTrue(stringid.startsWith("701230"));
        }
    }


    @Test(expected = NextProtException.class)
    public void shouldThrowAnExceptionWhenTheDatabaseIsunknown() {
       new XRefIdGenerator("Whaterver", "560/sites/51").build(dictionary);
    }


}