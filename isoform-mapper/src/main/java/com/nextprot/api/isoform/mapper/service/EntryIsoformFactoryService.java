package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.EntryIsoform;

public interface EntryIsoformFactoryService {

    EntryIsoform createsEntryIsoform(String accession);
}
