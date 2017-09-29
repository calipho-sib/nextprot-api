package org.nextprot.api.web.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutorServiceExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.nextprot.api.web.service.GraphQlExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@ConditionalOnMissingBean(GraphQlExecutor.class)
@Component
public class GraphQlExecutorImpl implements GraphQlExecutor {

    /*@Autowired
    private GraphQlExecutorProperties processorProperties;

    @Autowired
    private ObjectMapper jacksonObjectMapper;*/

/*    @Autowired
    private GraphQlSchemaBuilder schemaBuilder;*/

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
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        /*ExecutionResult executionResult = build.execute("{hello}");

        System.out.println(executionResult.getData().toString());*/


    }


    /*
    protected ExecutionStrategy createQueryExecutionStrategy() {
        return createExecutionStrategy(
                processorProperties.getMinimumThreadPoolSizeQuery(),
                processorProperties.getMaximumThreadPoolSizeQuery(),
                processorProperties.getKeepAliveTimeInSecondsQuery(),
                "graphql-query-thread-"
        );
    }

    protected ExecutionStrategy createMutationExecutionStrategy() {
        return createExecutionStrategy(
                processorProperties.getMinimumThreadPoolSizeMutation(),
                processorProperties.getMaximumThreadPoolSizeMutation(),
                processorProperties.getKeepAliveTimeInSecondsMutation(),
                "graphql-mutation-thread-"
        );
    }

    protected ExecutionStrategy createSubscriptionExecutionStrategy() {
        return createExecutionStrategy(
                processorProperties.getMinimumThreadPoolSizeSubscription(),
                processorProperties.getMaximumThreadPoolSizeSubscription(),
                processorProperties.getKeepAliveTimeInSecondsSubscription(),
                "graphql-subscription-thread-"
        );
    }
*/
    private ExecutionStrategy createExecutionStrategy(Integer minimumThreadPoolSize, Integer maximumThreadPoolSize, Integer keepAliveTimeInSeconds, String threadNamePrefix) {
        return new ExecutorServiceExecutionStrategy(new ThreadPoolExecutor(
                minimumThreadPoolSize,
                maximumThreadPoolSize,
                keepAliveTimeInSeconds,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new CustomizableThreadFactory(threadNamePrefix),
                new ThreadPoolExecutor.CallerRunsPolicy())
        );
    }

    protected void beforeExecuteRequest(String query, String operationName, Map<String, Object> context, Map<String, Object> variables) {
    }

    @Override
    public Object executeRequest(Map requestBody) {
        String query = (String) requestBody.get("query");
        String operationName = (String) requestBody.get("operationName");
        Map<String, Object> variables = getVariablesFromRequest(requestBody);
        Map<String, Object> context = new HashMap<>();

        beforeExecuteRequest(query, operationName, context, variables);
        ExecutionResult executionResult = graphQL.execute(query, operationName, context, variables);

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