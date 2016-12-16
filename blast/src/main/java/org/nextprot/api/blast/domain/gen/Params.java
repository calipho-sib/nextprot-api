
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
    "sequence",
    "matrix",
    "expect",
    "gap_open",
    "gap_extend",
    "filter",
    "cbs"
})
public class Params implements Serializable
{

    @JsonProperty("sequence")
    private String sequence;
    @JsonProperty("matrix")
    private String matrix;
    @JsonProperty("expect")
    private Double expect;
    @JsonProperty("gap_open")
    private Integer gapOpen;
    @JsonProperty("gap_extend")
    private Integer gapExtend;
    @JsonProperty("filter")
    private String filter;
    @JsonProperty("cbs")
    private Integer cbs;
    private final static long serialVersionUID = 597508011523172766L;

    @JsonProperty("sequence")
    public String getSequence() {
        return sequence;
    }

    @JsonProperty("sequence")
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

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

    /**
     * 
     * @return
     *     The expect
     */
    @JsonProperty("expect")
    public Double getExpect() {
        return expect;
    }

    /**
     * 
     * @param expect
     *     The expect
     */
    @JsonProperty("expect")
    public void setExpect(Double expect) {
        this.expect = expect;
    }

    /**
     * 
     * @return
     *     The gapOpen
     */
    @JsonProperty("gap_open")
    public Integer getGapOpen() {
        return gapOpen;
    }

    /**
     * 
     * @param gapOpen
     *     The gap_open
     */
    @JsonProperty("gap_open")
    public void setGapOpen(Integer gapOpen) {
        this.gapOpen = gapOpen;
    }

    /**
     * 
     * @return
     *     The gapExtend
     */
    @JsonProperty("gap_extend")
    public Integer getGapExtend() {
        return gapExtend;
    }

    /**
     * 
     * @param gapExtend
     *     The gap_extend
     */
    @JsonProperty("gap_extend")
    public void setGapExtend(Integer gapExtend) {
        this.gapExtend = gapExtend;
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

    /**
     * 
     * @return
     *     The cbs
     */
    @JsonProperty("cbs")
    public Integer getCbs() {
        return cbs;
    }

    /**
     * 
     * @param cbs
     *     The cbs
     */
    @JsonProperty("cbs")
    public void setCbs(Integer cbs) {
        this.cbs = cbs;
    }
}
