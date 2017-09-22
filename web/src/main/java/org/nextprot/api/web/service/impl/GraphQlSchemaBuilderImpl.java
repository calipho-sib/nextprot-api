package org.nextprot.api.web.service.impl;

/**
 * Created by dteixeir on 21.09.17.
 */

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.nextprot.api.web.domain.GraphQlFields;
import org.nextprot.api.web.domain.GraphQlProperties;
import org.nextprot.api.web.service.GraphQlSchemaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;


import org.springframework.stereotype.Service;

@Service
public class GraphQlSchemaBuilderImpl implements GraphQlSchemaBuilder {

    private GraphQlProperties properties;
    private List<GraphQlFields> graphQlFieldsDefinitions;

    @Override
    public GraphQLSchema getSchema() {
        return schema;
    }

    public void setSchema(GraphQLSchema schema) {
        this.schema = schema;
    }

    private GraphQLSchema schema;

    @Autowired
    public GraphQlSchemaBuilderImpl(GraphQlProperties properties, List<GraphQlFields> graphQlFieldsDefinitions) {
        this.properties = properties;
        this.graphQlFieldsDefinitions = graphQlFieldsDefinitions;
    }

    @PostConstruct
    public void postConstruct() {

        GraphQLObjectType.Builder queryBuilder = newObject().name(properties.getRootQueryName());
        GraphQLObjectType.Builder mutationBuilder = newObject().name(properties.getRootMutationName());

        if (StringUtils.hasText(properties.getRootQueryDescription())) {
            queryBuilder = queryBuilder.description(properties.getRootQueryDescription());
        }

        if (StringUtils.hasText(properties.getRootMutationDescription())) {
            mutationBuilder = mutationBuilder.description(properties.getRootMutationDescription());
        }

        buildSchemaFromDefinitions(queryBuilder, mutationBuilder);
    }

    private void buildSchemaFromDefinitions(GraphQLObjectType.Builder queryBuilder, GraphQLObjectType.Builder mutationBuilder) {
        boolean foundQueryDefinitions = false;
        boolean foundMutationDefinitions = false;

        for(val graphQlFieldsDefinition : graphQlFieldsDefinitions){

            val queryFields = graphQlFieldsDefinition.getQueryFields();
            if (queryFields != null && queryFields.size() > 0) {
                queryBuilder = queryBuilder.fields(queryFields);
                foundQueryDefinitions = true;
            }

            val mutationFields = graphQlFieldsDefinition.getMutationFields();
            if (mutationFields != null && mutationFields.size() > 0) {
                mutationBuilder = mutationBuilder.fields(mutationFields);
                foundMutationDefinitions = true;
            }
        }

        buildSchema(queryBuilder, mutationBuilder, foundQueryDefinitions, foundMutationDefinitions);
    }

    private void buildSchema(GraphQLObjectType.Builder queryBuilder, GraphQLObjectType.Builder mutationBuilder, boolean foundQueryDefinitions, boolean foundMutationDefinitions) {
        log.debug("Start building graphql schema");

        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();

        if (foundQueryDefinitions) {
            schemaBuilder = schemaBuilder.query(queryBuilder.build());
        }

        if (foundMutationDefinitions) {
            schemaBuilder = schemaBuilder.mutation(mutationBuilder.build());
        }

        if (foundQueryDefinitions || foundMutationDefinitions) {
            schema = schemaBuilder.build();
        } else {
            schema = generateGettingStartedGraphQlSchema();
        }
    }

    private GraphQLSchema generateGettingStartedGraphQlSchema() {
        val gettingStartedType = newObject()
                .name("gettingStartedQuery")
                .field(newFieldDefinition()
                        .type(GraphQLString)
                        .name("gettingStarted")
                        .staticValue("Create a component and implement GraphQlFields interface."))
                .build();

        return GraphQLSchema.newSchema()
                .query(gettingStartedType)
                .build();
    }
}