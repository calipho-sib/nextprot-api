
package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * POJO generated from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BlastOutput2"
})
public class BlastResult implements Serializable
{

    @JsonProperty("BlastOutput2")
    private List<BlastOutput2> blastOutput2 = null;
    private final static long serialVersionUID = -746574285921424667L;

    /**
     * 
     * @return
     *     The blastOutput2
     */
    @JsonProperty("BlastOutput2")
    public List<BlastOutput2> getBlastOutput2() {
        return blastOutput2;
    }

    /**
     * 
     * @param blastOutput2
     *     The BlastOutput2
     */
    @JsonProperty("BlastOutput2")
    public void setBlastOutput2(List<BlastOutput2> blastOutput2) {
        this.blastOutput2 = blastOutput2;
    }

    public static BlastResult fromJson(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, BlastResult.class);
    }
}
