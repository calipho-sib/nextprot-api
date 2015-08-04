package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;

public class PepXServiceTest extends WebIntegrationBaseTest {

    @Autowired
    private PepXService pepXService;

    @Test
    public void testPepXService() throws Exception {
    	List<Entry> accs = pepXService.findEntriesWithPeptides("IRLNK", true);
    	assertTrue(accs.size() > 0);
    }
    

}