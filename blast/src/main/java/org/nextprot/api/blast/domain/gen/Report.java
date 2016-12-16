
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
    "program",
    "version",
    "reference",
    "search_target",
    "params",
    "query_description",
    "results"
})
public class Report implements Serializable
{

    @JsonProperty("program")
    private String program;
    @JsonProperty("version")
    private String version;
    @JsonProperty("reference")
    private String reference;
    @JsonProperty("search_target")
    private SearchTarget searchTarget;
    @JsonProperty("params")
    private Params params;
    @JsonProperty("results")
    private Results results;
    private final static long serialVersionUID = -6965323630320685110L;

    /**
     * 
     * @return
     *     The program
     */
    @JsonProperty("program")
    public String getProgram() {
        return program;
    }

    /**
     * 
     * @param program
     *     The program
     */
    @JsonProperty("program")
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The reference
     */
    @JsonProperty("reference")
    public String getReference() {
        return reference;
    }

    /**
     * 
     * @param reference
     *     The reference
     */
    @JsonProperty("reference")
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * 
     * @return
     *     The searchTarget
     */
    @JsonProperty("search_target")
    public SearchTarget getSearchTarget() {
        return searchTarget;
    }

    /**
     * 
     * @param searchTarget
     *     The search_target
     */
    @JsonProperty("search_target")
    public void setSearchTarget(SearchTarget searchTarget) {
        this.searchTarget = searchTarget;
    }

    /**
     * 
     * @return
     *     The params
     */
    @JsonProperty("params")
    public Params getParams() {
        return params;
    }

    /**
     * 
     * @param params
     *     The params
     */
    @JsonProperty("params")
    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * 
     * @return
     *     The results
     */
    @JsonProperty("results")
    public Results getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    @JsonProperty("results")
    public void setResults(Results results) {
        this.results = results;
    }
}
