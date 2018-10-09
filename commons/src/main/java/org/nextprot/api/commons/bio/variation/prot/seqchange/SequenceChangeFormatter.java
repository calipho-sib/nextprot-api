package org.nextprot.api.commons.bio.variation.prot.seqchange;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Format change as String
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeFormatter<C extends SequenceChange<?>> {

    /**
     * Format the specified change C as String
     * @param sb the String container where formatted String should go
     * @param change the ProteinSequenceChange change to format
     * @param type the amino-acid code type
     */
    void format(StringBuilder sb, C change, AminoAcidCode.CodeType type);
}
