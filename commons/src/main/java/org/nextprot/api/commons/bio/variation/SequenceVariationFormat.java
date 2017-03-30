package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.impl.SequenceVariationImpl;

import java.text.ParseException;
import java.util.Collection;

/**
 * Provides contract for formatting and parsing <code>SequenceVariation</code>s
 *
 * Created by fnikitin on 07/09/15.
 */
public abstract class SequenceVariationFormat implements SequenceVariationFormatter<String> {

    public enum ParsingMode { STRICT, PERMISSIVE }

    /**
     * Formats a <code>ProteinSequenceVariation</code>.
     *
     * @param variation the variation to format
     * @param type the aa letter code type
     *
     * @return a formatter <code>String</>
     */
    @Override
    public String format(SequenceVariation variation, AminoAcidCode.CodeType type) {

        StringBuilder sb = new StringBuilder(prefixFormatter());

        // format changing amino acids part
        getChangingSequenceFormatter()
                .format(variation, type, sb);

        // format change part
        //noinspection unchecked
        getSequenceChangeFormat(variation.getSequenceChange().getType())
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

        SequenceVariationImpl.FluentBuilding builder = new SequenceVariationImpl.FluentBuilding();

        try {
            return parseWithMode(source, builder, ParsingMode.STRICT);
        } catch (ParseException e) {

            if (parsingMode == ParsingMode.PERMISSIVE)
                return parseWithMode(source, builder, ParsingMode.PERMISSIVE);

            throw e;
        }
    }

    private SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, ParsingMode mode) throws ParseException {

        for (SequenceChange.Type changeType : getAvailableChangeTypes()) {

            SequenceChangeFormat format = getSequenceChangeFormat(changeType);

            if (format.matchesWithMode(source, mode))
                return format.parseWithMode(source, builder, mode);
        }

        throw new ParseException(source + ": not a valid protein sequence variant", 0);
    }

    // prefix the protein sequence format
    protected String prefixFormatter() { return ""; }

    // get the changing sequence formatter
    protected abstract ChangingSequenceFormatter getChangingSequenceFormatter();

    // get the specific object handling formatting and parsing of sequence change
    protected abstract SequenceChangeFormat getSequenceChangeFormat(SequenceChange.Type changeType);
    protected abstract Collection<SequenceChange.Type> getAvailableChangeTypes();
}
