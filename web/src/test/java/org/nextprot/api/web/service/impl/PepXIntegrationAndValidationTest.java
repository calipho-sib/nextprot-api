package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;

public class PepXIntegrationAndValidationTest extends WebIntegrationBaseTest {

    @Autowired
    private PepXService pepXService;

    @Autowired
    private EntryBuilderService entryBuilderService;
    
    @Test
    public void testPepXService() throws Exception {
    	List<Entry> entries = pepXService.findEntriesWithPeptides("LIMINA", true);
    	assertFalse(entries.isEmpty());
    }

    
    @Test
    public void testPepXServiceWithAFalseAminoAcid() throws Exception {
    	List<Entry> entries = pepXService.findEntriesWithPeptides("LUMINA", true);
    	assertTrue(entries.isEmpty());
    }


}