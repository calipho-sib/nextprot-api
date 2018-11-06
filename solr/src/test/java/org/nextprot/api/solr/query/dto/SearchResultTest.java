package org.nextprot.api.solr.query.dto;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by fnikitin on 13/10/15.
 */
public class SearchResultTest {

    @Test
    public void testSpellcheckCollationPlusIsRemoved() throws Exception {

        SearchResult.Spellcheck spellcheck = new SearchResult.Spellcheck();

        spellcheck.addCollation("+insulin", 21);

        Map<String, Object> map = spellcheck.getCollations().iterator().next();

        Assert.assertEquals("insulin", map.get(SearchResult.Spellcheck.COLLATION_QUERY));
    }
}