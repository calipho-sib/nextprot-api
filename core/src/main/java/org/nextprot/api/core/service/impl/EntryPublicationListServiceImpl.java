package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.service.EntryPublicationListService;
import org.nextprot.api.core.service.EntryPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntryPublicationListServiceImpl implements EntryPublicationListService {

    @Autowired
    private MasterIdentifierDao masterIdentifierDao;

    @Autowired
    private EntryPublicationService entryPublicationService;

    private Map<Long, List<EntryPublication>> entryPublicationsById;

    @Cacheable("entry-publication-list-by-pubid")
    @Override
    public List<EntryPublication> getEntryPublicationListByPubId(long pubId) {

        if (entryPublicationsById == null) {
            entryPublicationsById = buildEntryPublicationsMap();
        }

        return entryPublicationsById.getOrDefault(pubId, new ArrayList<>());
    }

    // Memoized function that returns EntryPublications by publication id
    private Map<Long, List<EntryPublication>> buildEntryPublicationsMap() {

        Map<Long, List<EntryPublication>> map = new HashMap<>();

        for (String entryAccession : masterIdentifierDao.findUniqueNames()) {

            Map<Long, EntryPublication> publicationsById = entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationsById();

            for (Map.Entry<Long, EntryPublication> kv : publicationsById.entrySet()) {

                map.computeIfAbsent(kv.getKey(), k -> new ArrayList<>())
                        .add(kv.getValue());
            }
        }

        return map;
    }
}
