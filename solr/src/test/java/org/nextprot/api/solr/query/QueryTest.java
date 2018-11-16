package org.nextprot.api.solr.query;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.SolrCvCore;
import org.nextprot.api.solr.core.impl.SolrGoldAndSilverEntryCore;
import org.nextprot.api.solr.core.impl.SolrPublicationCore;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.schema.PublicationSolrField;

public class QueryTest {

    @Test
    public void testEscapingColon() {
        Query<CvSolrField> query = new Query<>(new SolrCvCore(""));
        query.addQuery("GO:0031017");
        Assert.assertEquals("GO\\:0031017", query.getQueryStringEscapeColon());
    }

    @Test
    public void testNotEscapingColonTwice() {
	    Query<CvSolrField> query = new Query<>(new SolrCvCore(""));
        query.addQuery("GO\\:0031017");
        Assert.assertEquals("GO\\:0031017", query.getQueryStringEscapeColon());
    }

    @Test
    public void testEscapingMultipleColons() {
	    Query<CvSolrField> query = new Query<>(new SolrCvCore(""));
        query.addQuery("GO:0031017:kokoko");
        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryStringEscapeColon());
    }

    @Test
    public void testNotEscapingMultipleColonsTwice() {
	    Query<CvSolrField> query = new Query<>(new SolrCvCore(""));
        query.addQuery("GO\\:0031017\\:kokoko");
        Assert.assertEquals("GO\\:0031017\\:kokoko", query.getQueryStringEscapeColon());
    }
    
    @Test
    public void testNotEscapingAuthorAndReplacingWithItsPrivateName() {
	    Query<PublicationSolrField> query = new Query<>(new SolrPublicationCore(""));
        query.addQuery("author:bairoch");
        Assert.assertEquals("authors:bairoch", query.getQueryStringEscapeColon());
    }

    @Test
    public void testNotEscapingMultipleFields() {
	    Query<PublicationSolrField> query = new Query<>(new SolrPublicationCore(""));
	    query.addQuery("author:bairoch title:nextprot year:2004");
        Assert.assertEquals("authors:bairoch title:nextprot year:2004", query.getQueryStringEscapeColon());
    }
    
    @Test
    public void testNotEscapingMultipleFieldsButEscapingGo() {
	    Query<PublicationSolrField> query = new Query<>(new SolrPublicationCore(""));
	    query.addQuery("author:bairoch title:nextprot GO:218374 year:2004");
        Assert.assertEquals("authors:bairoch title:nextprot GO\\:218374 year:2004", query.getQueryStringEscapeColon());
    }

    @Test
    public void testEscapingIrrelevantFieldForIndex() {
	    Query<PublicationSolrField> query = new Query<>(new SolrPublicationCore(""));
        query.addQuery("author:bairoch");
        Assert.assertEquals("author\\:bairoch", query.getQueryStringEscapeColon());
    }
    
    @Test
    public void testNotEscapingAnithingButReplacingFieldsWithPrivateName() {
	    Query<PublicationSolrField> query = new Query<>(new SolrPublicationCore(""));
	    query.addQuery("author:bairoch title:nextprot GO:218374 year:2004");
        Assert.assertEquals("authors:bairoch title:nextprot GO:218374 year:2004", query.getQueryString());
    }

    @Test
    public void testNotEscapingEntryIdField() {
	    Query<EntrySolrField> query = new Query<>(new SolrGoldAndSilverEntryCore(""));
	    query.addQuery("id:NX_A0P322");
        Assert.assertEquals("id:NX_A0P322", query.getQueryStringEscapeColon());
    }

    @Test
    public void test1() {
	    Query<EntrySolrField> query = new Query<>(new SolrGoldAndSilverEntryCore(""));
        query.addQuery("+insulin");
        String result = query.getQueryStringEscapeColon();
        Assert.assertEquals("+insulin", result);
    }
}