package com.nextprot.api.isoform.mapper.utils;

import com.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class IsoformSequencePositionMapperTest extends IsoformMappingBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    /*
NX_P38398-3, pos=1812
NX_P38398-4, pos=1812
NX_P38398-5, pos=1812
NX_P38398-6, pos=1812 -> nucleotides not in frame
NX_P38398-2, pos=1812 -> no map as expected
NX_P38398-7, pos=1812
NX_P38398-8, pos=1812
        */
    // TODO: missing specifications for "nucleotides not in frame" case (see User/story "Check not in frame")
    @Ignore
    @Test
    public void getProjectedPositionNotInFrame() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P38398").withTargetIsoforms());

        Integer position = IsoformSequencePositionMapper.getProjectedPosition(
                IsoformUtils.getIsoformByName(entry, "NX_P38398-1"), 1812,
                IsoformUtils.getIsoformByName(entry, "NX_P38398-6"));

        Assert.assertNotNull(position);
    }

    @Test
    public void positionOnIso1ShouldNotMapToIso2() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P38398").withTargetIsoforms());

        Integer position = IsoformSequencePositionMapper.getProjectedPosition(
                IsoformUtils.getIsoformByName(entry, "NX_P38398-1"), 1812,
                IsoformUtils.getIsoformByName(entry, "NX_P38398-2"));

        Assert.assertNull(position);
    }
}