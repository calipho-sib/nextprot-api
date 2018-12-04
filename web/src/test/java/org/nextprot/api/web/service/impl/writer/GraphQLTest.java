package org.nextprot.api.web.service.impl.writer;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.nextprot.api.core.domain.Entry;
import org.springframework.stereotype.Component;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

/**
 * Created by dteixeir on 22.09.17.
 */
public class GraphQLTest {


    @Component
    public class Query implements GraphQLQueryResolver {

        public Entry entry(String accession) {

            if(accession.equals("P01308")){
                return  new Entry("P01308");
            }else return  new Entry("P01307");

        }

    }


    public static void main(String[] args) {
        String schema = "type Query{hello: String} schema{query: Query}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute("{hello}");

        //System.out.println(executionResult.getData().toString());
        // Prints: {hello=world}
    }
}
