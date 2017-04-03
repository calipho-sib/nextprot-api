package org.nextprot.api.commons.bio.variation;

/**
 * Exception thrown when reference sequence is missing while building
 * a change that needs it
 */
public class MissingProteinSequenceException extends Exception {

    public MissingProteinSequenceException(SequenceVariationBuilder builder) {
        super("Reference protein sequence is missing in builder "+ builder.getClass().getSimpleName());
    }
}
