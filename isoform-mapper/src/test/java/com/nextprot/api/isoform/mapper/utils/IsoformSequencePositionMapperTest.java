package com.nextprot.api.isoform.mapper.utils;

import com.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import com.nextprot.api.isoform.mapper.service.EntryIsoformFactoryService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IsoformSequencePositionMapperTest extends IsoformMappingBaseTest {

    @Autowired
    private EntryIsoformFactoryService entryIsoformFactoryService;

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

        EntryIsoform entryIsoform = entryIsoformFactoryService.createsEntryIsoform("NX_P38398");

        Integer position = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(), 1812, entryIsoform.getIsoformByName("NX_P38398-6"));
        Assert.assertNotNull(position);
    }

    @Test
    public void positionOnIso1ShouldNotMapToIso2() throws Exception {

        EntryIsoform entryIsoform = entryIsoformFactoryService.createsEntryIsoform("NX_P38398");

        Integer position = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(), 1812, entryIsoform.getIsoformByName("NX_P38398-2"));
        Assert.assertNull(position);
    }
}