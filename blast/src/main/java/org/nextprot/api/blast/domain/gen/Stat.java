
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
    "db_num",
    "db_len",
    "hsp_len",
    "eff_space",
    "kappa",
    "lambda",
    "entropy"
})
public class Stat implements Serializable
{

    @JsonProperty("db_num")
    private int dbNum;
    @JsonProperty("db_len")
    private int dbLen;
    @JsonProperty("hsp_len")
    private Integer hspLen;
    @JsonProperty("eff_space")
    private Long effSpace;
    @JsonProperty("kappa")
    private Double kappa;
    @JsonProperty("lambda")
    private Double lambda;
    @JsonProperty("entropy")
    private Double entropy;
    private final static long serialVersionUID = 7214475728681675836L;

    /**
     * 
     * @return
     *     The dbNum
     */
    @JsonProperty("db_num")
    public int getDbNum() {
        return dbNum;
    }

    /**
     * 
     * @param dbNum
     *     The db_num
     */
    @JsonProperty("db_num")
    public void setDbNum(int dbNum) {
        this.dbNum = dbNum;
    }

    /**
     * 
     * @return
     *     The dbLen
     */
    @JsonProperty("db_len")
    public int getDbLen() {
        return dbLen;
    }

    /**
     * 
     * @param dbLen
     *     The db_len
     */
    @JsonProperty("db_len")
    public void setDbLen(int dbLen) {
        this.dbLen = dbLen;
    }

    /**
     * 
     * @return
     *     The hspLen
     */
    @JsonProperty("hsp_len")
    public Integer getHspLen() {
        return hspLen;
    }

    /**
     * 
     * @param hspLen
     *     The hsp_len
     */
    @JsonProperty("hsp_len")
    public void setHspLen(Integer hspLen) {
        this.hspLen = hspLen;
    }

    /**
     * 
     * @return
     *     The effSpace
     */
    @JsonProperty("eff_space")
    public Long getEffSpace() {
        return effSpace;
    }

    /**
     * 
     * @param effSpace
     *     The eff_space
     */
    @JsonProperty("eff_space")
    public void setEffSpace(Long effSpace) {
        this.effSpace = effSpace;
    }

    /**
     * 
     * @return
     *     The kappa
     */
    @JsonProperty("kappa")
    public Double getKappa() {
        return kappa;
    }

    /**
     * 
     * @param kappa
     *     The kappa
     */
    @JsonProperty("kappa")
    public void setKappa(Double kappa) {
        this.kappa = kappa;
    }

    /**
     * 
     * @return
     *     The lambda
     */
    @JsonProperty("lambda")
    public Double getLambda() {
        return lambda;
    }

    /**
     * 
     * @param lambda
     *     The lambda
     */
    @JsonProperty("lambda")
    public void setLambda(Double lambda) {
        this.lambda = lambda;
    }

    /**
     * 
     * @return
     *     The entropy
     */
    @JsonProperty("entropy")
    public Double getEntropy() {
        return entropy;
    }

    /**
     * 
     * @param entropy
     *     The entropy
     */
    @JsonProperty("entropy")
    public void setEntropy(Double entropy) {
        this.entropy = entropy;
    }
}
