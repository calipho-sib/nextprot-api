package org.nextprot.api.core.service.impl;

import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({ "dev" })
public class GenomicMappingServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private GenomicMappingService genomicMappingService;

    @Test
    public void findGenomicMappingsByEntryName() {

        //genomicMappingService.findGenomicMappingsByEntryName("NX_Q5JQC4");
        List<GenomicMapping> gm = genomicMappingService.findGenomicMappingsByEntryName("NX_P52701");
        System.out.println(gm.size());
    }
}