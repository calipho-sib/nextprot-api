package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev" })
public class EntryExonMappingServiceIntegrationTest extends CoreUnitBaseTest {
        
    @Autowired
	private EntryExonMappingService entryExonMappingService;

    @Test
    public void NX_Q96JG8_3DoesMapSomeTranscript() {

        ExonMapping mapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_Q96JG8");

        Assert.assertTrue(mapping.getMappedIsoformInfos().containsKey("NX_Q96JG8-3"));
        Assert.assertTrue(!mapping.getNonMappedIsoforms().contains("NX_Q96JG8-3"));
    }
    
    
    // 
    @Test
    public void NX_Q5TIA1_4_DoesNotMapAnyTranscript() {

        ExonMapping mapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_Q5TIA1");

        Assert.assertTrue(!mapping.getMappedIsoformInfos().containsKey("NX_Q5TIA1-4"));
        Assert.assertTrue(mapping.getNonMappedIsoforms().contains("NX_Q5TIA1-4"));
    }
    
}

