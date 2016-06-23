package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;

public class MappedIsoformFeatureResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        MAPPED, UNMAPPED
    }

    private String isoformName;
    private Integer firstIsoSeqPos;
    private Integer lastIsoSeqPos;
    private String message;
    private Status status;

    public String getIsoformName() {
        return isoformName;
    }

    public void setIsoformName(String isoformName) {
        this.isoformName = isoformName;
    }

    public Integer getFirstIsoSeqPos() {
        return firstIsoSeqPos;
    }

    public void setFirstIsoSeqPos(Integer firstIsoSeqPos) {
        this.firstIsoSeqPos = firstIsoSeqPos;
    }

    public Integer getLastIsoSeqPos() {
        return lastIsoSeqPos;
    }

    public void setLastIsoSeqPos(Integer lastIsoSeqPos) {
        this.lastIsoSeqPos = lastIsoSeqPos;
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

    /*
     In case of success

     {
         "query": {
            "accession": "NX_Q9UI33",
            "is-canonical": true,
            "feature": "SCN11A-p.Leu1158Pro",
            "feature-type": "VARIANT",
            "propagate": true
         },
        "success": true,
        "data": {
            "NX_Q9UI33-1": {
                "mapped": true,
                "range": {
                    "begin": 1158,
                    "end": 1158
                }
            },
            "NX_Q9UI33-2": {
                "mapped": false,
                "range": {}
            },
            "NX_Q9UI33-3": {
                "mapped": true,
                "range": {
                    "begin": 1120,
                    "end": 1120
                }
            },
        }
     }

     In case of error

     {
        "query": {
            "accession": "NX_P01308",
            "is-canonical": true,
            "feature": "SCN11A-p.Leu1158Pro",
            "feature-type": "VARIANT",
            "propagate": false
         },
        "success": false,
        "error": {
            "message": "Invalid ..."
            "type": "INVALID_POSITION(pos)" or "UNEXPECTED_AA(expected, observed)"
        }
     }
     */
}
