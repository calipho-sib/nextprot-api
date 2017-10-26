package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.EntryPublicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EntryPublicationServiceImpl implements EntryPublicationService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Cacheable("entry-publications")
    @Override
    public EntryPublications getEntryPublications(String entryAccession) {

        return EntryPublicationUtils.fetchEntryPublications(entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything()));
    }
}
