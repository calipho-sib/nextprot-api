package org.nextprot.api.web.service.impl.graphql;

import graphql.language.Argument;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EntryDataFetcherImpl implements DataFetcher<Entry> {


    @Autowired
    EntryBuilderService entryBuilderService;

    @Override
    public Entry get(DataFetchingEnvironment environment) {
        String accession = environment.getArgument("accession");

        Integer publicationLimit = -1;
        String category = "";

        //Searching for publication limit
        Optional<Selection> publicationField = environment.getFields().get(0).getSelectionSet().getSelections().stream().filter(f -> ((Field) f).getName().equals("publications")).findAny();
        if(publicationField.isPresent()){
            Optional<Argument> publicationLimitArg = ((Field)publicationField.get()).getArguments().stream().filter(f -> f.getName().equals("limit")).findAny();
            if(publicationLimitArg.isPresent()){
                publicationLimit = Integer.valueOf(publicationLimitArg.get().getValue().toString().replace("IntValue{value=", "").replace("}", ""));
            }
        }


        //Searching for annotation field
        Optional<Selection> annotationField = environment.getFields().get(0).getSelectionSet().getSelections().stream().filter(f -> ((Field) f).getName().equals("annotations")).findAny();
        if(annotationField.isPresent()){
            Optional<Argument> publicationLimitArg = ((Field)annotationField.get()).getArguments().stream().filter(f -> f.getName().equals("category")).findAny();
            if(publicationLimitArg.isPresent()){
                category = publicationLimitArg.get().getValue().toString().replace("StringValue{value='", "").replace("'}", "");
            }
        }

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(accession).with(category).withPublications().withOverview().withTargetIsoforms());

        if(publicationLimit != -1 ){
            List<Publication> publications = entry.getPublications();
            List<Publication> publicationSubset = publications.subList(0, publicationLimit);
            entry.setPublications(publicationSubset);
        }

        return entry;
    }
}