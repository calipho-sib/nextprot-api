package org.nextprot.api.commons.bio.variation.prot;

public class SequenceVariationBuildException extends Exception {

    public SequenceVariationBuildException(Throwable t) {

        super("Cannot build SequenceVariation instance: "+t.getMessage());
    }
}
