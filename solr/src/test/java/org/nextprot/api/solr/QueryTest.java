package org.nextprot.api.solr;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.solr.core.GoldAndSilverEntryCore;
import org.nextprot.api.solr.core.PublicationCore;
import org.nextprot.api.solr.core.SolrCore;

public class QueryTest {

    @Test
    public void testEscapingColon() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("GO:0031017");
        Assert.assertEquals("GO\\:0031017", query.getQueryString(true));
    }

    @Test
    public void testNotEscapingColonTwice() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("GO\\:0031017");
        Assert.assertEquals("GO\\:0031017", query.getQueryString(true));
    }

    @Test
    public void testEscapingMultipleColons() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("GO:0031017:kokoko");
        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryString(true));
    }

    @Test
    public void testNotEscapingMultipleColonsTwice() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("GO\\:0031017\\:kokoko");
        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryString(true));
    }
    
    @Test
    public void testNotEscapingAuthorAndReplacingWithItsPrivateName() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("author:bairoch");
        Assert.assertEquals("authors:bairoch", query.getQueryString(true));
    }

    @Test
    public void testNotEscapingMultipleFields() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("author:bairoch title:nextprot year:2004");
        Assert.assertEquals("authors:bairoch title:nextprot year:2004", query.getQueryString(true));
    }
    
    @Test
    public void testNotEscapingMultipleFieldsButEscapingGo() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("author:bairoch title:nextprot GO:218374 year:2004");
        System.out.println(query.getQueryString());
        Assert.assertEquals("authors:bairoch title:nextprot GO\\:218374 year:2004", query.getQueryString(true));
    }
    
    @Test
    public void testEscapingIrrelevantFieldForIndex() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new GoldAndSilverEntryCore());
        query.addQuery("author:bairoch");
        System.out.println(query.getQueryString());
        Assert.assertEquals("author\\:bairoch", query.getQueryString(true));
    }
    
    @Test
    public void testNotEscapingAnithingButReplacingFieldsWithPrivateName() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new PublicationCore());
        query.addQuery("author:bairoch title:nextprot GO:218374 year:2004");
        Assert.assertEquals("authors:bairoch title:nextprot GO:218374 year:2004", query.getQueryString(false));
    }

    @Test
    public void testNotEscapingEntryIdField() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new GoldAndSilverEntryCore());
        query.addQuery("id:NX_A0P322");
        Assert.assertEquals("id:NX_A0P322", query.getQueryString(true));
    }

    @Test
    public void test1() {
        Query query = new Query(Mockito.mock(SolrCore.class));
        query.setSolrCore(new GoldAndSilverEntryCore());
        query.addQuery("+insulin");
        String result = query.getQueryString(true);
        System.out.println(result);
        Assert.assertEquals("+insulin", result);
    }

    
    
}