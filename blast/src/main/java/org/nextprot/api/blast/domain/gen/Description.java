
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
    "title",
    "entry_accession",
    "isoform_accession",
    "isoform_name",
    "protein_name",
    "gene_name"
})
public class Description implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("accession")
    private String accession;
    @JsonProperty("title")
    private String title;
    @JsonProperty("entry_accession")
    private String entryAccession;
    @JsonProperty("isoform_accession")
    private String isoAccession;
    @JsonProperty("isoform_name")
    private String isoName;
    @JsonProperty("protein_name")
    private String proteinName;
    @JsonProperty("gene_name")
    private String geneName;
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

    @JsonProperty("entry_accession")
    public String getEntryAccession() {
        return entryAccession;
    }

    @JsonProperty("entry_accession")
    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    @JsonProperty("isoform_accession")
    public String getIsoAccession() {
        return isoAccession;
    }

    @JsonProperty("isoform_accession")
    public void setIsoAccession(String isoAccession) {
        this.isoAccession = isoAccession;
    }

    @JsonProperty("isoform_name")
    public String getIsoName() {
        return isoName;
    }

    @JsonProperty("isoform_name")
    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }
    @JsonProperty("protein_name")
    public String getProteinName() {
        return proteinName;
    }

    @JsonProperty("protein_name")
    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    @JsonProperty("gene_name")
    public String getGeneName() {
        return geneName;
    }

    @JsonProperty("gene_name")
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }
}
