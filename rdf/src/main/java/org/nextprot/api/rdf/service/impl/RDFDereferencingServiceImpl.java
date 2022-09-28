package org.nextprot.api.rdf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.api.rdf.service.RDFDereferencingService;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.impl.SparqlQueryDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RDFDereferencingServiceImpl implements RDFDereferencingService {

    private static final Log LOGGER = LogFactory.getLog(RdfHelpServiceImpl.class);

    @Value("${sparql.url}")
    private String sparqlEndpoint;

    @Autowired
    private SparqlQueryDictionary sparqlQueryDictionary;

    private final HttpSparqlService sparqlService;

    private Map<String,UserQuery> queryMap;

    private final String DEREF_CLASS_TAG = "deref_class_";

    private final String DEREF_INSTANCE_TAG = "deref_instance_";

    private final String NEXTPROT_CLASS_NAMESPACE = "http://nextprot.org/rdf#";

    private final String NEXTPROT_INSTANCE_NAMESPACE = "http://nextprot.org/rdf/";

    public RDFDereferencingServiceImpl(HttpSparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    @PostConstruct
    private void loadQueries(){
        // Extracts the sparql queries for dereferencing from nextprot queries and generates a query map by title
        List<UserQuery> queries = sparqlQueryDictionary.getSparqlQueryList();

        queryMap = queries.stream()
                .filter(q -> q.getTags()
                        .stream()
                        .map(t -> t.contains(DEREF_INSTANCE_TAG) || t.contains(DEREF_CLASS_TAG))
                        .reduce((a,b) -> a && b)
                        .orElse(false))
                .collect(Collectors.toMap(userQuery -> ((String) userQuery.getTags().toArray()[0]).toLowerCase(),
                        userQuery -> userQuery));
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
        if(accession.isPresent()) {
            // Instance query to be generated
            // Check for specific queries
            // Format of deref query tags are deref_instance_[entity] if entity exists
            // Or get the default query with tag deref_instance_
            UserQuery query = queryMap.get((DEREF_INSTANCE_TAG + entity).toLowerCase());
            if(query == null) {
                // Use default query
                query = queryMap.get(DEREF_INSTANCE_TAG.toLowerCase());
            }
            return generateInstanceQueryString(query, entity, accession.get());
        } else {
            // Class query to be generated
            // Check for specific queries
            // Format of deref query tags are deref_class_[entity] if entity exists
            // Or get the default query with tag deref_class_
            UserQuery query = queryMap.get((DEREF_CLASS_TAG + entity).toLowerCase());
            if(query == null) {
                // Use default query
                query = queryMap.get(DEREF_CLASS_TAG.toLowerCase());
            }
            return generateClassQueryString(query, entity);
        }
    }

    private String generateClassQueryString(UserQuery query, String entity) {
        return query.getSparql().replace("CLASS", NEXTPROT_CLASS_NAMESPACE + entity);
    }

    private String generateInstanceQueryString(UserQuery query, String entity, String accession) {
        return query.getSparql().replace("INSTANCE", NEXTPROT_INSTANCE_NAMESPACE + entity.toLowerCase() + "/" + accession);
    }
}
