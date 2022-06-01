package org.nextprot.api.rdf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.api.rdf.service.RDFDereferencingService;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.impl.SparqlQueryDictionary;
import org.nextprot.api.user.utils.UserQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class RDFDereferencingServiceImpl implements RDFDereferencingService {

    private static final Log LOGGER = LogFactory.getLog(RdfHelpServiceImpl.class);

    @Value("${sparql.url}")
    private String sparqlEndpoint;

    @Autowired
    private SparqlQueryDictionary sparqlQueryDictionary;

    private final HttpSparqlService sparqlService;

    private HashMap<String,UserQuery> queryMap;

    private final String DEREF_CLASS_TAG = "deref_class_";

    private final String DEREF_INSTANCE_TAG = "deref_instance_";

    private final String DEFAULT_CLASS_QUERY = "DESCRIBE :Entity";

    public RDFDereferencingServiceImpl(HttpSparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    @PostConstruct
    private void loadQueries(){
        // Extracts the sparql queries for dereferencing from nextprot queries and generates a query map by title
        List<UserQuery> queries = sparqlQueryDictionary.getSparqlQueryList();
        queryMap = new HashMap<>();
        UserQueryUtils.filterByTag(queries, DEREF_CLASS_TAG)
                .stream()
                .forEach(query -> queryMap.put(DEREF_CLASS_TAG +  query.getTitle(), query));

        UserQueryUtils.filterByTag(queries, DEREF_INSTANCE_TAG)
                .stream()
                .forEach(query -> queryMap.put(DEREF_INSTANCE_TAG +  query.getTitle(), query));
        LOGGER.info("Dereferencing queires loaded: " + queryMap.keySet().size());
    }

    @Override
    public String generateRDFContent(String entity, Optional<String> accession, String contentType) {

        if(entity == null) {
            return null;
        }

        String queryString = generateQueryString(entity, accession);
        if(queryString != null) {
            LOGGER.info("Query string generated " + queryString);
            return sparqlService.executeSparqlQuery(sparqlEndpoint,
                    queryString, contentType);
        } else {
            LOGGER.error("No query string generated");
            return null;
        }

    }

    private String generateQueryString(String entity, Optional<String> accession) {

        // Should handle both Class and Instance definitions
        if(!accession.isPresent()) {
            // Class query to be generated
            return generateClassQueryString(entity);
        } else {
            // Instance query to be generated
            return null;
        }
    }

    private String generateClassQueryString(String entity) {
        UserQuery query = queryMap.get(DEREF_CLASS_TAG + entity);
        if(query != null) {
            return query.getSparql();
        } else {
            return DEFAULT_CLASS_QUERY.replace("Entity", entity);
        }
    }

    private String generateInstanceQueryString(String entity, String accession) {
        return null;
    }
}
