package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.OverallEntryPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OverallEntryPublicationServiceImpl implements OverallEntryPublicationService {

    @Autowired
    private EntryPublicationService entryPublicationService;

    @Autowired
    private MasterIdentifierDao masterIdentifierDao;

    @Cacheable("entry-publications-by-pubid")
    @Override
    public Map<Long, List<EntryPublication>> findAllEntryPublications() {

        Map<Long, List<EntryPublication>> entryPublicationsById = new HashMap<>();

        for (String entryAccession : masterIdentifierDao.findUniqueNames()) {

            Map<Long, EntryPublication> publicationsById = entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationsById();

            for (Map.Entry<Long, EntryPublication> kv : publicationsById.entrySet()) {

                entryPublicationsById.computeIfAbsent(kv.getKey(), k -> new ArrayList<>())
                        .add(kv.getValue());
            }
        }

        return entryPublicationsById;
    }
}
