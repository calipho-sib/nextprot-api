package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.text.ParseException;

/**
 * <code>ProteinMutationHGVFormat</code> can format and parse
 * ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class ProteinMutationHGVFormat extends AbstractProteinMutationFormat {

    public enum ParsingMode { STRICT, PERMISSIVE }

    private final MutatedAAsFormat mutatedAAsFormat = new MutatedAAsHGVFormat();
    private final InsertionHGVFormat insertionHGVFormat = new InsertionHGVFormat();
    private final SubstitutionHGVFormat subtitutionHGVFormat = new SubstitutionHGVFormat();
    private final DeletionHGVFormat deletionHGVFormat = new DeletionHGVFormat();
    private final DeletionInsertionHGVFormat deletionInsertionHGVFormat = new DeletionInsertionHGVFormat();
    private final FrameshiftHGVFormat frameshiftHGVFormat = new FrameshiftHGVFormat();

    @Override
    protected MutatedAAsFormat getAffectedAAsFormat() {
        return mutatedAAsFormat;
    }

    @Override
    protected MutationEffectFormat<Substitution> getSubstitutionFormat() {
        return subtitutionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Insertion> getInsertionFormat() {
        return insertionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Deletion> getDeletionFormat() {
        return deletionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<DeletionAndInsertion> getDeletionInsertionFormat() {
        return deletionInsertionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Frameshift> getFrameshiftFormat() {
        return frameshiftHGVFormat;
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

        ProteinMutation mutation = subtitutionHGVFormat.parseWithMode(source, builder, mode);

        if (mutation == null) mutation = deletionHGVFormat.parseWithMode(source, builder, mode);
        if (mutation == null) mutation = frameshiftHGVFormat.parseWithMode(source, builder, mode);
        if (mutation == null) mutation = deletionInsertionHGVFormat.parseWithMode(source, builder, mode);
        if (mutation == null) mutation = insertionHGVFormat.parseWithMode(source, builder, mode);

        if (mutation == null) throw new ParseException(source + " is not a valid protein mutation", 0);

        return mutation;
    }

    static AminoAcidCode valueOfAminoAcidCode(String code1, String code2and3) {

        if (code2and3 == null) return AminoAcidCode.valueOfCode(code1);

        return AminoAcidCode.valueOfCode(code1+code2and3);
    }
}
