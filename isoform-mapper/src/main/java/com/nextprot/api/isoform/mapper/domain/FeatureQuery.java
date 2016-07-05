package com.nextprot.api.isoform.mapper.domain;

/**
 * Created by fnikitin on 05/07/16.
 */
public interface FeatureQuery {

    String getAccession();

    String getFeature();

    String getFeatureType();

    boolean isFeaturePropagable();
}
