package org.nextprot.api.commons.bio.variation.prot;

public class VariationOutOfSequenceBoundException extends Exception {

    public VariationOutOfSequenceBoundException(int position, int length) {

        super("Variation at position "+ position+" is out of sequence bounds (length="+length+")");
    }
}
