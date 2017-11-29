package org.nextprot.api.core.domain.publication;

import org.junit.Assert;
import org.junit.Test;

public class PublicationTypeTest {

    @Test
    public void testAllIds() throws Exception {

        Assert.assertEquals(10, PublicationType.ARTICLE.getId());
        Assert.assertEquals(20, PublicationType.PATENT.getId());
        Assert.assertEquals(30, PublicationType.BOOK.getId());
        Assert.assertEquals(40, PublicationType.THESIS.getId());
        Assert.assertEquals(50, PublicationType.SUBMISSION.getId());
        Assert.assertEquals(60, PublicationType.ONLINE_PUBLICATION.getId());
        Assert.assertEquals(70, PublicationType.UNPUBLISHED_OBSERVATION.getId());
        Assert.assertEquals(80, PublicationType.DOCUMENT.getId());
    }

    @Test
    public void testValueOfAllExistingNames() throws Exception {

        for (PublicationType type : PublicationType.values()) {

            Assert.assertEquals(type, PublicationType.valueOfName(type.name()));
        }
        Assert.assertEquals(PublicationType.UNPUBLISHED_OBSERVATION, PublicationType.valueOfName("UNPUBLISHED OBSERVATION"));
        Assert.assertEquals(PublicationType.ONLINE_PUBLICATION, PublicationType.valueOfName("ONLINE PUBLICATION"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfUnknownName() throws Exception {

        PublicationType.valueOfName("UNPUBLIS");
    }

    @Test
    public void testValueOfAllExistingIds() throws Exception {

        Assert.assertEquals(PublicationType.ARTICLE, PublicationType.valueOfId(10));
        Assert.assertEquals(PublicationType.PATENT, PublicationType.valueOfId(20));
        Assert.assertEquals(PublicationType.BOOK, PublicationType.valueOfId(30));
        Assert.assertEquals(PublicationType.THESIS, PublicationType.valueOfId(40));
        Assert.assertEquals(PublicationType.SUBMISSION, PublicationType.valueOfId(50));
        Assert.assertEquals(PublicationType.ONLINE_PUBLICATION, PublicationType.valueOfId(60));
        Assert.assertEquals(PublicationType.UNPUBLISHED_OBSERVATION, PublicationType.valueOfId(70));
        Assert.assertEquals(PublicationType.DOCUMENT, PublicationType.valueOfId(80));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfUnknownId() throws Exception {

        PublicationType.valueOfId(2);
    }
}