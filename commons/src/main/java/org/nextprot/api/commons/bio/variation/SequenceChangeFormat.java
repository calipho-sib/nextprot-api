package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.text.ParseException;

/**
 * Specify how change C is formatted as String and parsed to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeFormat<C extends SequenceChange> {

    /**
     * Format the specified change C as String
     * @param sb the String container where formatted String should go
     * @param change the ProteinSequenceChange change to format
     * @param type the amino-acid code type
     */
    void format(StringBuilder sb, C change, AminoAcidCode.AACodeType type);

    /**
     * Parse the source and build ProteinSequenceVariation with given builder
     * @param source the formatted change
     * @param builder the builder to build ProteinSequenceVariation
     * @param mode the parsing mode
     * @return an instance of ProteinSequenceVariation
     * @throws ParseException if parsing error occurs
     */
    SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder,
                                    SequenceVariationFormat.ParsingMode mode) throws ParseException;
    /**
     * Attempts to match the source.
     * @return  <tt>true</tt> if matches the source
     */
    boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode);
}
