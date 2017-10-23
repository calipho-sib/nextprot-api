package org.nextprot.api.web.service.impl.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO to replace EntryDataFetcherImpl
//@Service
public class EntryQueryResolver implements GraphQLQueryResolver {

  /*  @Autowired
    EntryBuilderService entryBuilderService;

    public Entry getEntry(String accession) {
        return entryBuilderService.build(EntryConfig.newConfig(accession).withEverything());
    }

    */
}

