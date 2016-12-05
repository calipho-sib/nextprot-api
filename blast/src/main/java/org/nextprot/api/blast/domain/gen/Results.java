
package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * POJO generated from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "search"
})
public class Results implements Serializable
{

    @JsonProperty("search")
    private Search search;
    private final static long serialVersionUID = 8456797271594564975L;

    /**
     * 
     * @return
     *     The search
     */
    @JsonProperty("search")
    public Search getSearch() {
        return search;
    }

    /**
     * 
     * @param search
     *     The search
     */
    @JsonProperty("search")
    public void setSearch(Search search) {
        this.search = search;
    }

    public Results withSearch(Search search) {
        this.search = search;
        return this;
    }

}
