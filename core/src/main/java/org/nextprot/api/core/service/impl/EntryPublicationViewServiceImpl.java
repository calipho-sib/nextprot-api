package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublicationView;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationCategory;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.EntryPublicationViewService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EntryPublicationViewServiceImpl implements EntryPublicationViewService {

    @Autowired
    private EntryPublicationService entryPublicationService;

    @Autowired
    private PublicationService publicationService;

    @Override
    public List<EntryPublicationView> buildEntryPublicationView(String entryAccession, PublicationCategory publicationCategory) {

        EntryPublications entryPublications = entryPublicationService.findEntryPublications(entryAccession);

        List<EntryPublicationView> list = new ArrayList<>();

        Map<Long, EntryPublication> entryPublicationMap = entryPublications
                .getEntryPublicationList(publicationCategory).stream()
                .collect(Collectors.toMap(
                        EntryPublication::getPubId,
                        Function.identity(),
                        (entryPublication, entryPublication2) -> entryPublication
                ));

        List<Publication> publications = publicationService.findPublicationsByEntryName(entryPublications.getEntryAccession());

        for (Publication publication : publications) {

            if (entryPublicationMap.containsKey(publication.getPublicationId())) {
                EntryPublicationView view = new EntryPublicationView();

                EntryPublication entryPublication = entryPublicationMap.get(publication.getPublicationId());

                view.setCitedInViews(entryPublication.getCitedInViews());
                view.setDirectLinks(entryPublication.getDirectLinks());
                view.setPublication(publication);

                list.add(view);
            }
        }

        return list;
    }
}
