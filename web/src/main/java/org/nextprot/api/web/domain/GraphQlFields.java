package org.nextprot.api.web.domain;

import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * Created by dteixeir on 21.09.17.
 */
public interface GraphQlFields {
    List<GraphQLFieldDefinition> getQueryFields();
    List<GraphQLFieldDefinition> getMutationFields();
}