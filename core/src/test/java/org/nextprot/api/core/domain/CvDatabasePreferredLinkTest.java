package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CvDatabasePreferredLinkTest {

    @Test
    public void testGetCvDatabasePreferredLinks() throws Exception {

        List<CvDatabasePreferredLink> list = CvDatabasePreferredLink.getCvDatabasePreferredLinks("ensembl");

        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testDbHasPreferredLinks() throws Exception {

        Assert.assertTrue(CvDatabasePreferredLink.dbHasPreferredLink("ensembl"));
    }

    @Test
    public void testGetCvDatabasePreferredLinksUnknownDb() throws Exception {

        List<CvDatabasePreferredLink> list = CvDatabasePreferredLink.getCvDatabasePreferredLinks("kokolasticot");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testDbHasNotPreferredLinks() throws Exception {

        Assert.assertTrue(!CvDatabasePreferredLink.dbHasPreferredLink("Uniprot"));
    }
}