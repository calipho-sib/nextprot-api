
package org.nextprot.api.blast.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "max_score",
        "total_score",
        "min_evalue",
        "identity_percent"
})
public class GlobalHit implements Serializable
{
    private final static long serialVersionUID = 1L;

    @JsonProperty("max_score")
    private int maxScore;

    @JsonProperty("total_score")
    private int totalScore;

    @JsonProperty("min_evalue")
    private double minEvalue;

    @JsonProperty("identity_percent")
    private Float identityPercent;

    @JsonProperty("max_score")
    public int getMaxScore() {
        return maxScore;
    }

    @JsonProperty("max_score")
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    @JsonProperty("total_score")
    public int getTotalScore() {
        return totalScore;
    }

    @JsonProperty("total_score")
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @JsonProperty("min_evalue")
    public double getMinEvalue() {
        return minEvalue;
    }

    @JsonProperty("min_evalue")
    public void setMinEvalue(double minEvalue) {
        this.minEvalue = minEvalue;
    }

    @JsonProperty("identity_percent")
    public Float getIdentityPercent() {
        return identityPercent;
    }

    @JsonProperty("identity_percent")
    public void setIdentityPercent(Float globalIdentityPercent) {
        this.identityPercent = globalIdentityPercent;
    }
}
