
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
    "db"
})
public class SearchTarget implements Serializable
{

    @JsonProperty("db")
    private String db;
    private final static long serialVersionUID = 4664082278225295183L;

    /**
     * 
     * @return
     *     The db
     */
    @JsonProperty("db")
    public String getDb() {
        return db;
    }

    /**
     * 
     * @param db
     *     The db
     */
    @JsonProperty("db")
    public void setDb(String db) {
        this.db = db;
    }
}
