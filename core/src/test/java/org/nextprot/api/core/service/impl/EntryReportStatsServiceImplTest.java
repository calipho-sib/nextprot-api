package org.nextprot.api.core.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class EntryReportStatsServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private EntryReportStatsService entryReportStatsService;

    @Test
    public void NX_P01574IsMutagenesis() {

        EntryReportStats stats = entryReportStatsService.reportEntryStats("NX_P01574");

        Assert.assertTrue(stats.isMutagenesis());
    }

    @Test
    public void NX_O15498IsMutagenesis() {

        EntryReportStats stats = entryReportStatsService.reportEntryStats("NX_O15498");

        Assert.assertTrue(stats.isMutagenesis());
    }

    @Test
    public void NX_Q9UBP0IsMutagenesis() {

        EntryReportStats stats = entryReportStatsService.reportEntryStats("NX_Q9UBP0");

        Assert.assertTrue(stats.isMutagenesis());
    }

    @Test
    public void NX_Q9UJT9IsNotMutagenesis() {

        EntryReportStats stats = entryReportStatsService.reportEntryStats("NX_Q9UJT9");

        Assert.assertFalse(stats.isMutagenesis());
    }
}