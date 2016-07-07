package com.nextprot.api.isoform.mapper.domain;

import java.text.ParseException;

public interface GeneFeaturePair {

    String getGeneName();
    IsoformFeature getFeature();

    interface GeneFeaturePairParser {
        GeneFeaturePair parse(String variant) throws ParseException;
    }
}
