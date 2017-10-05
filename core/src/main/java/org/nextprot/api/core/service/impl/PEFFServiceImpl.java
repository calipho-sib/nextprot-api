package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.impl.peff.IsoformPEFFHeaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PEFFServiceImpl implements PEFFService {

    @Autowired
    private EntryService entryService;

    @Autowired
    private TerminologyService terminologyService;

    @Override
    public IsoformPEFFHeader formatPEFFHeader(String isoformAccession) {
        
        return new IsoformPEFFHeaderBuilder(isoformAccession,
                entryService.findEntryFromIsoformAccession(isoformAccession), terminologyService).withEverything()
                .build();
    }
}
