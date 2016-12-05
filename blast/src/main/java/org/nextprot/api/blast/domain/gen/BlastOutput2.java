
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
    "report"
})
public class BlastOutput2 implements Serializable
{

    @JsonProperty("report")
    private Report report;
    private final static long serialVersionUID = 731745532384463470L;

    /**
     * 
     * @return
     *     The report
     */
    @JsonProperty("report")
    public Report getReport() {
        return report;
    }

    /**
     * 
     * @param report
     *     The report
     */
    @JsonProperty("report")
    public void setReport(Report report) {
        this.report = report;
    }

    public BlastOutput2 withReport(Report report) {
        this.report = report;
        return this;
    }

}
