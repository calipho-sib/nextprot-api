
package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nextprot.api.blast.domain.GlobalHit;

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
    "hsps",
    "global_hit"
})
public class Hit implements Serializable
{

    @JsonProperty("num")
    private Integer num;
    @JsonProperty("description")
    private List<Description> description = null;
    @JsonProperty("len")
    private int len;
    @JsonProperty("hsps")
    private List<Hsp> hsps = null;
    @JsonProperty("global")
    private GlobalHit globalHit;

    private final static long serialVersionUID = 3L;

    /**
     * 
     * @return
     *     The num
     */
    @JsonProperty("num")
    public Integer getNum() {
        return num;
    }

    /**
     * 
     * @param num
     *     The num
     */
    @JsonProperty("num")
    public void setNum(Integer num) {
        this.num = num;
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

    @JsonProperty("global")
    public GlobalHit getGlobalHit() {
        return globalHit;
    }

    @JsonProperty("global")
    public void setGlobalHit(GlobalHit globalHit) {
        this.globalHit = globalHit;
    }
}
