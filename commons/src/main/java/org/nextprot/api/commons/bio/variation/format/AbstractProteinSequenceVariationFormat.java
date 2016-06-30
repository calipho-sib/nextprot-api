package org.nextprot.api.commons.bio.variation.format;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

    public enum ParsingMode { STRICT, PERMISSIVE }

    public String format(ProteinSequenceVariation mutation) {
        return format(mutation, AACodeType.ONE_LETTER);
    }

    @Override
    public String format(ProteinSequenceVariation variation, AACodeType type) {

        StringBuilder sb = new StringBuilder();

        // format affected amino acids
        getChangingAAsFormat().format(sb, variation, type);

        // format mutation
        if (variation.getProteinSequenceChange() instanceof Deletion) getDeletionFormat().format(sb, (Deletion) variation.getProteinSequenceChange(), type);
        else if (variation.getProteinSequenceChange() instanceof Substitution) getSubstitutionFormat().format(sb, (Substitution) variation.getProteinSequenceChange(), type);
        else if (variation.getProteinSequenceChange() instanceof DeletionAndInsertion) getDeletionInsertionFormat().format(sb, (DeletionAndInsertion) variation.getProteinSequenceChange(), type);
        else if (variation.getProteinSequenceChange() instanceof Insertion) getInsertionFormat().format(sb, (Insertion) variation.getProteinSequenceChange(), type);
        else if (variation.getProteinSequenceChange() instanceof Duplication) getDuplicationFormat().format(sb, (Duplication) variation.getProteinSequenceChange(), type);
        else if (variation.getProteinSequenceChange() instanceof Frameshift) getFrameshiftFormat().format(sb, (Frameshift) variation.getProteinSequenceChange(), type);

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

    public boolean isValidProteinSequenceVariant(String source) {

        return source.startsWith("p.");
    }

    private ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, ParsingMode mode) throws ParseException {

        for (ProteinSequenceChangeFormat format : getFormats()) {

            if (format.matchesWithMode(source, mode))
                return format.parseWithMode(source, builder, mode);
        }

        throw new ParseException(source + " is not a valid protein mutation", 0);
    }

    // delegated formats
    protected abstract ChangingAAsFormat getChangingAAsFormat();
    protected abstract ProteinSequenceChangeFormat<Substitution> getSubstitutionFormat();
    protected abstract ProteinSequenceChangeFormat<Insertion> getInsertionFormat();
    protected abstract ProteinSequenceChangeFormat<Duplication> getDuplicationFormat();
    protected abstract ProteinSequenceChangeFormat<Deletion> getDeletionFormat();
    protected abstract ProteinSequenceChangeFormat<DeletionAndInsertion> getDeletionInsertionFormat();
    protected abstract ProteinSequenceChangeFormat<Frameshift> getFrameshiftFormat();

    private List<ProteinSequenceChangeFormat> getFormats() {
        List<ProteinSequenceChangeFormat> formats = new ArrayList<>();

        formats.add(getSubstitutionFormat());
        formats.add(getDeletionFormat());
        formats.add(getFrameshiftFormat());
        formats.add(getDeletionInsertionFormat());
        formats.add(getInsertionFormat());
        formats.add(getDuplicationFormat());

        return formats;
    }

    public static String formatAminoAcidCode(AACodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            if (type == AACodeType.ONE_LETTER) sb.append(String.valueOf(aa.get1LetterCode()));
            else sb.append(String.valueOf(aa.get3LetterCode()));
        }

        return sb.toString();
    }

    public static AminoAcidCode valueOfAminoAcidCode(String code1, String code2and3) throws ParseException {

        if (code2and3 == null) {
            if (!AminoAcidCode.isValidAminoAcid(code1)) {
                throw new ParseException(code1+": invalid AminoAcidCode", 0);
            }
            return AminoAcidCode.valueOfAminoAcid(code1);
        }
        else if (!AminoAcidCode.isValidAminoAcid(code1 + code2and3)) {
            throw new ParseException(code1 + code2and3 + ": invalid AminoAcidCode", 2);
        }
        return AminoAcidCode.valueOfAminoAcid(code1 + code2and3);
    }
}
