package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@ActiveProfiles("dev")
public class EntryPublicationDaoTest extends CoreUnitBaseTest {

    @Autowired
    private EntryPublicationDao entryPublicationDao;

    @Test
    public void findPublicationDirectLinkList() throws Exception {

        List<PublicationDirectLink> links = entryPublicationDao.findPublicationDirectLinks("NX_O75478")
                .get(660681L);

        Assert.assertEquals(3, links.size());
    }

    @Test
    public void findPublicationDirectLinks() throws Exception {

        Map<Long, List<PublicationDirectLink>> links = entryPublicationDao.findPublicationDirectLinks("NX_O75478");
        Assert.assertTrue(links.size() > 20);

    }

}