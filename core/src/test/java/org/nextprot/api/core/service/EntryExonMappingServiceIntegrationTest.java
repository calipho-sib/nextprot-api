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
    public void NX_Q96JG8_3DoesNotMapAnyTranscript() {

        ExonMapping mapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_Q96JG8");

        Assert.assertTrue(!mapping.getMappedIsoformInfos().get("NX_Q96JG8-3").containsKey("main-transcript"));
        Assert.assertTrue(!mapping.getMappedIsoformInfos().get("NX_Q96JG8-3").containsKey("other-transcripts"));
    }
}