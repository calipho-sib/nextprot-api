
package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

/**
 * POJO generated from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "query_id",
    "query_title",
    "query_len",
    "hits",
    "stat"
})
public class Search implements Serializable
{

    @JsonProperty("query_id")
    private String queryId;
    @JsonProperty("query_title")
    private String queryTitle;
    @JsonProperty("query_len")
    private Integer queryLen;
    @JsonProperty("hits")
    private List<Hit> hits = null;
    @JsonProperty("stat")
    private Stat stat;
    private final static long serialVersionUID = 4699115788781800260L;

    /**
     * 
     * @return
     *     The queryId
     */
    @JsonProperty("query_id")
    public String getQueryId() {
        return queryId;
    }

    /**
     * 
     * @param queryId
     *     The query_id
     */
    @JsonProperty("query_id")
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @JsonProperty("query_title")
    public String getQueryTitle() {
        return queryTitle;
    }

    /**
     *
     * @param queryTitle
     *     The query_title
     */
    @JsonProperty("query_title")
    public void setQueryTitle(String queryTitle) {
        this.queryTitle = queryTitle;
    }

    /**
     * 
     * @return
     *     The queryLen
     */
    @JsonProperty("query_len")
    public Integer getQueryLen() {
        return queryLen;
    }

    /**
     * 
     * @param queryLen
     *     The query_len
     */
    @JsonProperty("query_len")
    public void setQueryLen(Integer queryLen) {
        this.queryLen = queryLen;
    }


    /**
     * 
     * @return
     *     The hits
     */
    @JsonProperty("hits")
    public List<Hit> getHits() {
        return hits;
    }

    /**
     * 
     * @param hits
     *     The hits
     */
    @JsonProperty("hits")
    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public Search withHits(List<Hit> hits) {
        this.hits = hits;
        return this;
    }

    /**
     * 
     * @return
     *     The stat
     */
    @JsonProperty("stat")
    public Stat getStat() {
        return stat;
    }

    /**
     * 
     * @param stat
     *     The stat
     */
    @JsonProperty("stat")
    public void setStat(Stat stat) {
        this.stat = stat;
    }
}
