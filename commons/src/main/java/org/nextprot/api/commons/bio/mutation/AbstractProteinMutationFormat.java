package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.text.ParseException;

/**
 * A base class for parsing and formatting ProteinMutation
 *
 * Created by fnikitin on 07/09/15.
 */
public abstract class AbstractProteinMutationFormat implements ProteinMutationFormat {

    public enum ParsingMode { STRICT, PERMISSIVE }

    public String format(ProteinMutation mutation) {
        return format(mutation, AACodeType.ONE_LETTER);
    }

    @Override
    public String format(ProteinMutation mutation, AACodeType type) {

        StringBuilder sb = new StringBuilder();

        // affected amino acids
        getAffectedAAsFormat().format(sb, mutation, type);

        // mutation
        if (mutation.getMutation() instanceof Deletion) getDeletionFormat().format(sb, (Deletion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Substitution) getSubstitutionFormat().format(sb, (Substitution) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof DeletionAndInsertion) getDeletionInsertionFormat().format(sb, (DeletionAndInsertion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Insertion) getInsertionFormat().format(sb, (Insertion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Frameshift) getFrameshiftFormat().format(sb, (Frameshift) mutation.getMutation(), type);

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
    public ProteinMutation parse(String source) throws ParseException {

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
    public ProteinMutation parse(String source, ParsingMode parsingMode) throws ParseException {

        Preconditions.checkNotNull(source);
        Preconditions.checkArgument(source.startsWith("p."), "not a valid protein sequence variant");
        Preconditions.checkNotNull(parsingMode);

        ProteinMutation.FluentBuilder builder = new ProteinMutation.FluentBuilder();

        try {
            return parseWithMode(source, builder, ParsingMode.STRICT);
        } catch (ParseException e) {

            if (parsingMode == ParsingMode.PERMISSIVE)
                return parseWithMode(source, builder, ParsingMode.PERMISSIVE);

            throw e;
        }
    }

    private ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, ParsingMode mode) throws ParseException {

        ProteinMutation mutation = getSubstitutionFormat().parseWithMode(source, builder, mode);

        if (mutation == null) mutation = getDeletionFormat().parseWithMode(source, builder, mode);
        if (mutation == null) mutation = getFrameshiftFormat().parseWithMode(source, builder, mode);
        if (mutation == null) mutation = getDeletionInsertionFormat().parseWithMode(source, builder, mode);
        if (mutation == null) mutation = getInsertionFormat().parseWithMode(source, builder, mode);

        if (mutation == null) throw new ParseException(source + " is not a valid protein mutation", 0);

        return mutation;
    }

    // delegated formats
    protected abstract MutatedAAsFormat getAffectedAAsFormat();
    protected abstract MutationEffectFormat<Substitution> getSubstitutionFormat();
    protected abstract MutationEffectFormat<Insertion> getInsertionFormat();
    protected abstract MutationEffectFormat<Deletion> getDeletionFormat();
    protected abstract MutationEffectFormat<DeletionAndInsertion> getDeletionInsertionFormat();
    protected abstract MutationEffectFormat<Frameshift> getFrameshiftFormat();

    public static String formatAminoAcidCode(AACodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            if (type == AACodeType.ONE_LETTER) sb.append(String.valueOf(aa.get1LetterCode()));
            else sb.append(String.valueOf(aa.get3LetterCode()));
        }

        return sb.toString();
    }

    public static AminoAcidCode valueOfAminoAcidCode(String code1, String code2and3) {

        if (code2and3 == null) return AminoAcidCode.valueOfCode(code1);

        return AminoAcidCode.valueOfCode(code1 + code2and3);
    }
}
