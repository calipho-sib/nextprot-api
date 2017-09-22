package org.nextprot.api.web.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "com.merapar.graphql.executor")
public class GraphQlExecutorProperties {

    private Integer minimumThreadPoolSizeQuery = 10;
    private Integer maximumThreadPoolSizeQuery = 20;
    private Integer keepAliveTimeInSecondsQuery = 30;
    private Integer minimumThreadPoolSizeMutation = 10;

    public Integer getMinimumThreadPoolSizeQuery() {
        return minimumThreadPoolSizeQuery;
    }

    public void setMinimumThreadPoolSizeQuery(Integer minimumThreadPoolSizeQuery) {
        this.minimumThreadPoolSizeQuery = minimumThreadPoolSizeQuery;
    }

    public Integer getMaximumThreadPoolSizeQuery() {
        return maximumThreadPoolSizeQuery;
    }

    public void setMaximumThreadPoolSizeQuery(Integer maximumThreadPoolSizeQuery) {
        this.maximumThreadPoolSizeQuery = maximumThreadPoolSizeQuery;
    }

    public Integer getKeepAliveTimeInSecondsQuery() {
        return keepAliveTimeInSecondsQuery;
    }

    public void setKeepAliveTimeInSecondsQuery(Integer keepAliveTimeInSecondsQuery) {
        this.keepAliveTimeInSecondsQuery = keepAliveTimeInSecondsQuery;
    }

    public Integer getMinimumThreadPoolSizeMutation() {
        return minimumThreadPoolSizeMutation;
    }

    public void setMinimumThreadPoolSizeMutation(Integer minimumThreadPoolSizeMutation) {
        this.minimumThreadPoolSizeMutation = minimumThreadPoolSizeMutation;
    }

    public Integer getMaximumThreadPoolSizeMutation() {
        return maximumThreadPoolSizeMutation;
    }

    public void setMaximumThreadPoolSizeMutation(Integer maximumThreadPoolSizeMutation) {
        this.maximumThreadPoolSizeMutation = maximumThreadPoolSizeMutation;
    }

    public Integer getKeepAliveTimeInSecondsMutation() {
        return keepAliveTimeInSecondsMutation;
    }

    public void setKeepAliveTimeInSecondsMutation(Integer keepAliveTimeInSecondsMutation) {
        this.keepAliveTimeInSecondsMutation = keepAliveTimeInSecondsMutation;
    }

    public Integer getMinimumThreadPoolSizeSubscription() {
        return minimumThreadPoolSizeSubscription;
    }

    public void setMinimumThreadPoolSizeSubscription(Integer minimumThreadPoolSizeSubscription) {
        this.minimumThreadPoolSizeSubscription = minimumThreadPoolSizeSubscription;
    }

    public Integer getMaximumThreadPoolSizeSubscription() {
        return maximumThreadPoolSizeSubscription;
    }

    public void setMaximumThreadPoolSizeSubscription(Integer maximumThreadPoolSizeSubscription) {
        this.maximumThreadPoolSizeSubscription = maximumThreadPoolSizeSubscription;
    }

    public Integer getKeepAliveTimeInSecondsSubscription() {
        return keepAliveTimeInSecondsSubscription;
    }

    public void setKeepAliveTimeInSecondsSubscription(Integer keepAliveTimeInSecondsSubscription) {
        this.keepAliveTimeInSecondsSubscription = keepAliveTimeInSecondsSubscription;
    }

    private Integer maximumThreadPoolSizeMutation = 20;
    private Integer keepAliveTimeInSecondsMutation = 30;
    private Integer minimumThreadPoolSizeSubscription = 10;
    private Integer maximumThreadPoolSizeSubscription = 20;
    private Integer keepAliveTimeInSecondsSubscription = 30;

}