package org.nextprot.api.commons.bio.variation.seq.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

import java.text.ParseException;

/**
 * Provides contract for formatting and parsing <code>ProteinSequenceVariation</code>s
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ProteinSequenceVariationFormat {

    enum ParsingMode { STRICT, PERMISSIVE }

    /**
     * Formats a <code>ProteinSequenceVariation</code>.
     *
     * @param variation the variation to format
     * @param type the aa letter code type
     *
     * @return a formatter <code>String</>
     */
    String format(ProteinSequenceVariation variation, AminoAcidCode.AACodeType type);

    /**
     * Parses text from the beginning of the given string to produce a ProteinSequenceVariation.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>ProteinSequenceVariation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    ProteinSequenceVariation parse(String source) throws ParseException;
}
