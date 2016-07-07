package org.nextprot.api.commons.bio.variation.seq;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.impl.SequenceVariationImpl;

import java.text.ParseException;
import java.util.Collection;

/**
 * Provides contract for formatting and parsing <code>ProteinSequenceVariation</code>s
 * A base class for parsing and formatting ProteinMutation
 *
 * A ProteinMutation is composed of 2 parts:
 * - the changing part locating the amino-acids that change
 * - the variation itself
 *
 * Created by fnikitin on 07/09/15.
 */
public abstract class SequenceVariationFormat {

    public enum ParsingMode { STRICT, PERMISSIVE }

    public String format(SequenceVariation mutation) {
        return format(mutation, AminoAcidCode.AACodeType.ONE_LETTER);
    }

    /**
     * Formats a <code>ProteinSequenceVariation</code>.
     *
     * @param variation the variation to format
     * @param type the aa letter code type
     *
     * @return a formatter <code>String</>
     */
    public String format(SequenceVariation variation, AminoAcidCode.AACodeType type) {

        StringBuilder sb = new StringBuilder();

        // format changing amino acids part
        getChangingAAsFormat().format(sb, variation, type);

        // format change part
        //noinspection unchecked
        getChangeFormat(variation.getSequenceChange().getType())
                .format(sb, variation.getSequenceChange(), type);

        return sb.toString();
    }

    /**
     * Parses text from the beginning of the given string to produce a ProteinSequenceVariation.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>ProteinSequenceVariation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    public SequenceVariation parse(String source) throws ParseException {

        return parse(source, ParsingMode.STRICT);
    }

    /**
     * Parses text from the beginning of the given string to produce a ProteinMutation in permissive or strict mode
     *
     * @param source a standard or closely standard HGV text.
     * @param parsingMode if ParsingMode.PERMISSIVE accept slightly difference from the correct format else throw a ParseException
     * @return A <code>ProteinMutation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    public SequenceVariation parse(String source, ParsingMode parsingMode) throws ParseException {

        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(parsingMode);

        SequenceVariationImpl.FluentBuilder builder = new SequenceVariationImpl.FluentBuilder();

        try {
            return parseWithMode(source, builder, ParsingMode.STRICT);
        } catch (ParseException e) {

            if (parsingMode == ParsingMode.PERMISSIVE)
                return parseWithMode(source, builder, ParsingMode.PERMISSIVE);

            throw e;
        }
    }

    private SequenceVariation parseWithMode(String source, SequenceVariationImpl.FluentBuilder builder, ParsingMode mode) throws ParseException {

        for (SequenceChange.Type changeType : getAvailableChangeTypes()) {

            SequenceChangeFormat format = getChangeFormat(changeType);

            if (format.matchesWithMode(source, mode))
                return format.parseWithMode(source, builder, mode);
        }

        throw new ParseException(source + ": not a valid protein sequence variant", 0);
    }

    protected abstract ChangingAAsFormat getChangingAAsFormat();
    protected abstract SequenceChangeFormat getChangeFormat(SequenceChange.Type changeType);
    protected abstract Collection<SequenceChange.Type> getAvailableChangeTypes();
}
