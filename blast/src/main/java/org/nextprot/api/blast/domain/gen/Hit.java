
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
    "num",
    "description",
    "len",
    "hsps"
})
public class Hit implements Serializable
{

    @JsonProperty("num")
    private int num;
    @JsonProperty("description")
    private List<Description> description = null;
    @JsonProperty("len")
    private int len;
    @JsonProperty("hsps")
    private List<Hsp> hsps = null;
    private final static long serialVersionUID = 8937818461376471395L;

    /**
     * 
     * @return
     *     The num
     */
    @JsonProperty("num")
    public int getNum() {
        return num;
    }

    /**
     * 
     * @param num
     *     The num
     */
    @JsonProperty("num")
    public void setNum(int num) {
        this.num = num;
    }

    public Hit withNum(int num) {
        this.num = num;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public List<Description> getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(List<Description> description) {
        this.description = description;
    }

    public Hit withDescription(List<Description> description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return
     *     The len
     */
    @JsonProperty("len")
    public int getLen() {
        return len;
    }

    /**
     * 
     * @param len
     *     The len
     */
    @JsonProperty("len")
    public void setLen(int len) {
        this.len = len;
    }

    public Hit withLen(int len) {
        this.len = len;
        return this;
    }

    /**
     * 
     * @return
     *     The hsps
     */
    @JsonProperty("hsps")
    public List<Hsp> getHsps() {
        return hsps;
    }

    /**
     * 
     * @param hsps
     *     The hsps
     */
    @JsonProperty("hsps")
    public void setHsps(List<Hsp> hsps) {
        this.hsps = hsps;
    }

    public Hit withHsps(List<Hsp> hsps) {
        this.hsps = hsps;
        return this;
    }

}
