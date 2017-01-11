
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
    "num",
    "bit_score",
    "score",
    "evalue",
    "identity",
    "positive",
    "query_from",
    "query_to",
    "hit_from",
    "hit_to",
    "align_len",
    "gaps",
    "qseq",
    "hseq",
    "midline",
    "identity_percent",
})
public class Hsp implements Serializable
{

    @JsonProperty("num")
    private Integer num;
    @JsonProperty("bit_score")
    private double bitScore;
    @JsonProperty("score")
    private int score;
    @JsonProperty("evalue")
    private double evalue;
    @JsonProperty("identity")
    private int identity;
    @JsonProperty("positive")
    private int positive;
    @JsonProperty("query_from")
    private int queryFrom;
    @JsonProperty("query_to")
    private int queryTo;
    @JsonProperty("hit_from")
    private int hitFrom;
    @JsonProperty("hit_to")
    private int hitTo;
    @JsonProperty("align_len")
    private int alignLen;
    @JsonProperty("gaps")
    private int gaps;
    @JsonProperty("qseq")
    private String qseq;
    @JsonProperty("hseq")
    private String hseq;
    @JsonProperty("midline")
    private String midline;
    @JsonProperty("identity_percent")
    private Float identityPercent;
    private final static long serialVersionUID = 6273459601101276229L;

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
     *     The bitScore
     */
    @JsonProperty("bit_score")
    public double getBitScore() {
        return bitScore;
    }

    /**
     * 
     * @param bitScore
     *     The bit_score
     */
    @JsonProperty("bit_score")
    public void setBitScore(double bitScore) {
        this.bitScore = bitScore;
    }

    /**
     * 
     * @return
     *     The score
     */
    @JsonProperty("score")
    public int getScore() {
        return score;
    }

    /**
     * 
     * @param score
     *     The score
     */
    @JsonProperty("score")
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 
     * @return
     *     The evalue
     */
    @JsonProperty("evalue")
    public double getEvalue() {
        return evalue;
    }

    /**
     * 
     * @param evalue
     *     The evalue
     */
    @JsonProperty("evalue")
    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    /**
     * 
     * @return
     *     The identity
     */
    @JsonProperty("identity")
    public int getIdentity() {
        return identity;
    }

    /**
     * 
     * @param identity
     *     The identity
     */
    @JsonProperty("identity")
    public void setIdentity(int identity) {
        this.identity = identity;
    }

    /**
     * 
     * @return
     *     The positive
     */
    @JsonProperty("positive")
    public int getPositive() {
        return positive;
    }

    /**
     * 
     * @param positive
     *     The positive
     */
    @JsonProperty("positive")
    public void setPositive(int positive) {
        this.positive = positive;
    }

    /**
     * 
     * @return
     *     The queryFrom
     */
    @JsonProperty("query_from")
    public int getQueryFrom() {
        return queryFrom;
    }

    /**
     * 
     * @param queryFrom
     *     The query_from
     */
    @JsonProperty("query_from")
    public void setQueryFrom(int queryFrom) {
        this.queryFrom = queryFrom;
    }

    /**
     * 
     * @return
     *     The queryTo
     */
    @JsonProperty("query_to")
    public int getQueryTo() {
        return queryTo;
    }

    /**
     * 
     * @param queryTo
     *     The query_to
     */
    @JsonProperty("query_to")
    public void setQueryTo(int queryTo) {
        this.queryTo = queryTo;
    }

    /**
     * 
     * @return
     *     The hitFrom
     */
    @JsonProperty("hit_from")
    public int getHitFrom() {
        return hitFrom;
    }

    /**
     * 
     * @param hitFrom
     *     The hit_from
     */
    @JsonProperty("hit_from")
    public void setHitFrom(int hitFrom) {
        this.hitFrom = hitFrom;
    }

    /**
     * 
     * @return
     *     The hitTo
     */
    @JsonProperty("hit_to")
    public int getHitTo() {
        return hitTo;
    }

    /**
     * 
     * @param hitTo
     *     The hit_to
     */
    @JsonProperty("hit_to")
    public void setHitTo(int hitTo) {
        this.hitTo = hitTo;
    }

    /**
     * 
     * @return
     *     The alignLen
     */
    @JsonProperty("align_len")
    public int getAlignLen() {
        return alignLen;
    }

    /**
     * 
     * @param alignLen
     *     The align_len
     */
    @JsonProperty("align_len")
    public void setAlignLen(int alignLen) {
        this.alignLen = alignLen;
    }

    /**
     * 
     * @return
     *     The gaps
     */
    @JsonProperty("gaps")
    public int getGaps() {
        return gaps;
    }

    /**
     * 
     * @param gaps
     *     The gaps
     */
    @JsonProperty("gaps")
    public void setGaps(int gaps) {
        this.gaps = gaps;
    }

    /**
     * 
     * @return
     *     The qseq
     */
    @JsonProperty("qseq")
    public String getQseq() {
        return qseq;
    }

    /**
     * 
     * @param qseq
     *     The qseq
     */
    @JsonProperty("qseq")
    public void setQseq(String qseq) {
        this.qseq = qseq;
    }

    /**
     * 
     * @return
     *     The hseq
     */
    @JsonProperty("hseq")
    public String getHseq() {
        return hseq;
    }

    /**
     * 
     * @param hseq
     *     The hseq
     */
    @JsonProperty("hseq")
    public void setHseq(String hseq) {
        this.hseq = hseq;
    }

    /**
     * 
     * @return
     *     The midline
     */
    @JsonProperty("midline")
    public String getMidline() {
        return midline;
    }

    /**
     * 
     * @param midline
     *     The midline
     */
    @JsonProperty("midline")
    public void setMidline(String midline) {
        this.midline = midline;
    }

    @JsonProperty("identity_percent")
    public Float getIdentityPercent() {
        return identityPercent;
    }

    @JsonProperty("identity_percent")
    public void setIdentityPercent(Float identityPercent) {
        this.identityPercent = identityPercent;
    }
}
