package org.nextprot.api.commons.bio.variation.seq.format;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceChange;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

import java.text.ParseException;
import java.util.Collection;

/**
 * A base class for parsing and formatting ProteinMutation
 *
 * A ProteinMutation is composed of 2 parts:
 * - the changing part locating the amino-acids that change
 * - the variation itself
 *
 * Created by fnikitin on 07/09/15.
 */
public abstract class AbstractProteinSequenceVariationFormat implements ProteinSequenceVariationFormat {

    public String format(ProteinSequenceVariation mutation) {
        return format(mutation, AminoAcidCode.AACodeType.ONE_LETTER);
    }

    @Override
    public String format(ProteinSequenceVariation variation, AminoAcidCode.AACodeType type) {

        StringBuilder sb = new StringBuilder();

        // format changing amino acids part
        getChangingAAsFormat().format(sb, variation, type);

        // format change part
        //noinspection unchecked
        getChangeFormat(variation.getProteinSequenceChange().getType())
                .format(sb, variation.getProteinSequenceChange(), type);

        return sb.toString();
    }

    /**
     * Parses text from the beginning of the given string to produce a ProteinMutation in strict mode
     *
     * @param source a standard HGV text.
     * @return A <code>ProteinMutation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    @Override
    public ProteinSequenceVariation parse(String source) throws ParseException {

        return parse(source, ParsingMode.STRICT);
    }

    public boolean isValidProteinSequenceVariant(String source) {

        return source.startsWith("p.");
    }

    /**
     * Parses text from the beginning of the given string to produce a ProteinMutation in permissive or strict mode
     *
     * @param source a standard or closely standard HGV text.
     * @param parsingMode if ParsingMode.PERMISSIVE accept slightly difference from the correct format else throw a ParseException
     * @return A <code>ProteinMutation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    public ProteinSequenceVariation parse(String source, ParsingMode parsingMode) throws ParseException {

        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(parsingMode);

        if (!isValidProteinSequenceVariant(source)) {
            throw new ParseException(source + ": not a valid protein sequence variant", 0);
        }

        ProteinSequenceVariation.FluentBuilder builder = new ProteinSequenceVariation.FluentBuilder();

        try {
            return parseWithMode(source, builder, ParsingMode.STRICT);
        } catch (ParseException e) {

            if (parsingMode == ParsingMode.PERMISSIVE)
                return parseWithMode(source, builder, ParsingMode.PERMISSIVE);

            throw e;
        }
    }

    private ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, ParsingMode mode) throws ParseException {

        for (ProteinSequenceChange.Type changeType : getAvailableChangeTypes()) {

            ProteinSequenceChangeFormat format = getChangeFormat(changeType);

            if (format.matchesWithMode(source, mode))
                return format.parseWithMode(source, builder, mode);
        }

        throw new ParseException(source + " is not a valid protein mutation", 0);
    }

    protected abstract ChangingAAsFormat getChangingAAsFormat();
    protected abstract ProteinSequenceChangeFormat getChangeFormat(ProteinSequenceChange.Type changeType);
    protected abstract Collection<ProteinSequenceChange.Type> getAvailableChangeTypes();


}
