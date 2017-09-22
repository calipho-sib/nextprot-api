package org.nextprot.api.web.service;

import graphql.schema.GraphQLSchema;

public interface GraphQlSchemaBuilder {
    GraphQLSchema getSchema();
}