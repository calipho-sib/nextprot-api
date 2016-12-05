
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
    "id",
    "accession",
    "title"
})
public class Description implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("accession")
    private String accession;
    @JsonProperty("title")
    private String title;
    private final static long serialVersionUID = -3670991271422757868L;

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Description withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The accession
     */
    @JsonProperty("accession")
    public String getAccession() {
        return accession;
    }

    /**
     * 
     * @param accession
     *     The accession
     */
    @JsonProperty("accession")
    public void setAccession(String accession) {
        this.accession = accession;
    }

    public Description withAccession(String accession) {
        this.accession = accession;
        return this;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public Description withTitle(String title) {
        this.title = title;
        return this;
    }

}
