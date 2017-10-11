package org.nextprot.api.web.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.nextprot.api.web.service.GraphQlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@Component
public class GraphQlExecutorImpl implements GraphQlExecutor {

    @Autowired
    private DataFetcher entryDataFetcher;

    private TypeReference<HashMap<String, Object>> typeRefReadJsonString = new TypeReference<HashMap<String, Object>>() {
    };

    private GraphQL graphQL;

    @PostConstruct
    private void postConstruct() {


        SchemaParser schemaParser = new SchemaParser();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("swapi.graphqls").getFile());

        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(file);

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("entry", entryDataFetcher))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        graphQL = GraphQL.newGraphQL(graphQLSchema).build();

    }


    @Override
    public Object executeRequest(Map requestBody) {

        ExecutionResult executionResult = graphQL.execute((String) requestBody.get("query"));

        HashMap result = new LinkedHashMap<String, Object>();

        if (executionResult.getErrors().size() > 0) {
            result.put("errors", executionResult.getErrors());
            //log.error("Errors: {}", executionResult.getErrors());
        }
        result.put("data", executionResult.getData());

        return result;
    }

    private Map<String, Object> getVariablesFromRequest(Map requestBody) {
        Object variablesFromRequest = requestBody.get("variables");

        if (variablesFromRequest == null) {
            return Collections.emptyMap();
        }

        if (variablesFromRequest instanceof String) {
            if (StringUtils.hasText((String) variablesFromRequest)) {
                return getVariablesMapFromString((String) variablesFromRequest);
            }
        } else if (variablesFromRequest instanceof Map) {
            return (Map<String, Object>) variablesFromRequest;
        } else {
            throw new GraphQLException("Incorrect variables");
        }

        return Collections.emptyMap();
    }


    private Map<String, Object> getVariablesMapFromString(String variablesFromRequest) {
        try {
            return new ObjectMapper().readValue(variablesFromRequest, typeRefReadJsonString);
        } catch (IOException exception) {
            throw new GraphQLException("Cannot parse variables", exception);
        }
    }



}