
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
    "matrix",
    "expect",
    "gap_open",
    "gap_extend",
    "filter",
    "cbs"
})
public class Params implements Serializable
{

    @JsonProperty("matrix")
    private String matrix;
    @JsonProperty("expect")
    private int expect;
    @JsonProperty("gap_open")
    private int gapOpen;
    @JsonProperty("gap_extend")
    private int gapExtend;
    @JsonProperty("filter")
    private String filter;
    @JsonProperty("cbs")
    private int cbs;
    private final static long serialVersionUID = 597508011523172766L;

    /**
     * 
     * @return
     *     The matrix
     */
    @JsonProperty("matrix")
    public String getMatrix() {
        return matrix;
    }

    /**
     * 
     * @param matrix
     *     The matrix
     */
    @JsonProperty("matrix")
    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public Params withMatrix(String matrix) {
        this.matrix = matrix;
        return this;
    }

    /**
     * 
     * @return
     *     The expect
     */
    @JsonProperty("expect")
    public int getExpect() {
        return expect;
    }

    /**
     * 
     * @param expect
     *     The expect
     */
    @JsonProperty("expect")
    public void setExpect(int expect) {
        this.expect = expect;
    }

    public Params withExpect(int expect) {
        this.expect = expect;
        return this;
    }

    /**
     * 
     * @return
     *     The gapOpen
     */
    @JsonProperty("gap_open")
    public int getGapOpen() {
        return gapOpen;
    }

    /**
     * 
     * @param gapOpen
     *     The gap_open
     */
    @JsonProperty("gap_open")
    public void setGapOpen(int gapOpen) {
        this.gapOpen = gapOpen;
    }

    public Params withGapOpen(int gapOpen) {
        this.gapOpen = gapOpen;
        return this;
    }

    /**
     * 
     * @return
     *     The gapExtend
     */
    @JsonProperty("gap_extend")
    public int getGapExtend() {
        return gapExtend;
    }

    /**
     * 
     * @param gapExtend
     *     The gap_extend
     */
    @JsonProperty("gap_extend")
    public void setGapExtend(int gapExtend) {
        this.gapExtend = gapExtend;
    }

    public Params withGapExtend(int gapExtend) {
        this.gapExtend = gapExtend;
        return this;
    }

    /**
     * 
     * @return
     *     The filter
     */
    @JsonProperty("filter")
    public String getFilter() {
        return filter;
    }

    /**
     * 
     * @param filter
     *     The filter
     */
    @JsonProperty("filter")
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Params withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 
     * @return
     *     The cbs
     */
    @JsonProperty("cbs")
    public int getCbs() {
        return cbs;
    }

    /**
     * 
     * @param cbs
     *     The cbs
     */
    @JsonProperty("cbs")
    public void setCbs(int cbs) {
        this.cbs = cbs;
    }

    public Params withCbs(int cbs) {
        this.cbs = cbs;
        return this;
    }

}
