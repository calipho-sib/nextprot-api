package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;

/**
 * Validation result of location of feature on isoform and gene.
 * More precisely check location of changing amino-acids and nucleotides.
 *
 * Created by fnikitin on 22/06/16.
 */
public class IsoformFeature implements Serializable {

    private static final long serialVersionUID = 1L;

    // TODO: define status according to specifications
    // TODO: if not mapped give a precision of the step ...
    // http error code ? ...
    public enum Status {
        MAPPED, UNMAPPED
    }

    private String isoformName;
    private Integer firstPositionOnIsoform;
    private Integer lastPositionOnIsoform;
    private Integer firstPositionOnGene;
    private Integer lastPositionOnGene;
    private String message;
    private Status status;

    public String getIsoformName() {
        return isoformName;
    }

    public void setIsoformName(String isoformName) {
        this.isoformName = isoformName;
    }

    public Integer getFirstPositionOnIsoform() {
        return firstPositionOnIsoform;
    }

    public void setFirstPositionOnIsoform(Integer firstPositionOnIsoform) {
        this.firstPositionOnIsoform = firstPositionOnIsoform;
    }

    public Integer getLastPositionOnIsoform() {
        return lastPositionOnIsoform;
    }

    public void setLastPositionOnIsoform(Integer lastPositionOnIsoform) {
        this.lastPositionOnIsoform = lastPositionOnIsoform;
    }

    public Integer getFirstPositionOnGene() {
        return firstPositionOnGene;
    }

    public void setFirstPositionOnGene(Integer firstPositionOnGene) {
        this.firstPositionOnGene = firstPositionOnGene;
    }

    public Integer getLastPositionOnGene() {
        return lastPositionOnGene;
    }

    public void setLastPositionOnGene(Integer lastPositionOnGene) {
        this.lastPositionOnGene = lastPositionOnGene;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
