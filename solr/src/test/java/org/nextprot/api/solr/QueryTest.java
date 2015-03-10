package org.nextprot.api.solr;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class QueryTest {

    @Test
    public void testEscapingColonInQueryString() {

        Query query = new Query(Mockito.mock(SolrIndex.class));

        query.addQuery("GO:0031017");

        Assert.assertEquals("GO\\:0031017", query.getQueryString());
    }

    @Test
    public void testEscapingColonInQueryStringIdemPotence() {

        Query query = new Query(Mockito.mock(SolrIndex.class));

        query.addQuery("GO\\:0031017");

        Assert.assertEquals("GO\\:0031017", query.getQueryString());
    }

    @Test
    public void testEscapingMultipleColonInQueryString() {

        Query query = new Query(Mockito.mock(SolrIndex.class));

        query.addQuery("GO:0031017:kokoko");

        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryString());
    }

    @Test
    public void testEscapingMultipleColonInQueryStringIdemPotence() {

        Query query = new Query(Mockito.mock(SolrIndex.class));

        query.addQuery("GO\\:0031017\\:kokoko");

        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryString());
    }
}