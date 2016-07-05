package com.nextprot.api.isoform.mapper.domain;

public interface FeatureQuery {

    /**
     * @return the nextprot accession number of the isoform (example: NX_P01308 or NX_P01308-1)
     */
    String getAccession();

    /**
     * @return the string formatted feature
     */
    String getFeature();

    /**
     * @return the annotation category as string
     */
    String getFeatureType();

    /**
     * @return true if feature should be propagated to other isoforms
     */
    boolean isFeaturePropagable();
}
