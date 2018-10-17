package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GlobalPublicationServiceImpl implements GlobalPublicationService {

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @Autowired
    private PublicationService publicationService;

    @Override
    public Set<Long> findAllPublicationIds() {

        return masterIdentifierService.findUniqueNames().stream()
                .map(entryAccession -> publicationService.findPublicationsByEntryName(entryAccession))
                .flatMap(publications -> publications.stream())
                .map(publication -> publication.getPublicationId())
                .collect(Collectors.toSet());
    }
}
