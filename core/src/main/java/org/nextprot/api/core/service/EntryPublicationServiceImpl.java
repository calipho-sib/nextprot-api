package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublicationReport;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.EntryPublicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EntryPublicationServiceImpl implements EntryPublicationService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Cacheable("entry-publication-reports")
    @Override
    public EntryPublicationReport reportEntryPublication(String entryAccession) {

        return EntryPublicationUtils.buildReport(entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything()));
    }
}
