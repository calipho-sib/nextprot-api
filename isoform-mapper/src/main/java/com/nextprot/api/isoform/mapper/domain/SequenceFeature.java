package com.nextprot.api.isoform.mapper.domain;

import com.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformRuntimeException;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

/**
 * A sequence feature on an isoform sequence on a specific gene
 */
public interface SequenceFeature {

    /** @return the gene name */
    String getGeneName();

    /** @return isoform name or null if canonical */
    String getIsoformName();

    /** @return the variation as a string */
    String getFormattedVariation();

    /**
     * Format a feature specifically to isoform
     * @param isoform the specific isoform
     * @param firstAAPos first position of the feature
     * @param lastAAPos last position of the feature
     * @return a format of the feature on the isoform
     */
    String formatIsoSpecificFeature(Isoform isoform, int firstAAPos, int lastAAPos);

    /** @return the protein sequence variation */
    SequenceVariation getProteinVariation();

    /**
     * @param entry entry to validate gene name
     * @return true if gene name is referenced in entry
     */
    boolean isValidGeneName(Entry entry);

    /**
     * Check that current entry has isoform named getIsoformName()
     * @param entry entry to validate isoform
     * @return true if isoform is valid
     */
    boolean isValidIsoform(Entry entry);

    /**
     * Get specific entry isoform where lies the feature
     * @param entry entry from which isoform is accessed
     * @return the entry isoform
     * @throws UnknownIsoformRuntimeException
     */
    Isoform getIsoform(Entry entry) throws UnknownIsoformRuntimeException;
}
