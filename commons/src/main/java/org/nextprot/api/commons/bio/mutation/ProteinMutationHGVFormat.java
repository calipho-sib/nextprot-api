package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>ProteinMutationHGVFormat</code> can format and parse
 * ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class ProteinMutationHGVFormat implements ProteinMutationFormat {

    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)([A-Z])([a-z]{2})?$");
    private static final Pattern DELETION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del$");
    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)(\\d+)$");
    private static final Pattern DELETION_INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?delins((?:[A-Z]([a-z]{2})?)+)$");

    public String format(ProteinMutation mutation) {
        return format(mutation, AACodeType.ONE_LETTER);
    }

    @Override
    public String format(ProteinMutation mutation, AACodeType type) {

        StringBuilder sb = new StringBuilder();

        sb.append("p.");

        // affected amino-acid(s)
        sb.append(formatAminoAcidCode(type, mutation.getFirstAffectedAminoAcidCode()));
        sb.append(mutation.getFirstAffectedAminoAcidPos());
        if (mutation.isAminoAcidRange())
            sb.append("_").append(formatAminoAcidCode(type, mutation.getLastAffectedAminoAcidCode())).append(mutation.getLastAffectedAminoAcidPos());

        // mutation effect
        if (mutation.getMutation() instanceof Deletion) sb.append("del");
        else if (mutation.getMutation() instanceof Substitution) sb.append(formatAminoAcidCode(type, (AminoAcidCode)mutation.getMutation().getValue()));
        else if (mutation.getMutation() instanceof DeletionAndInsertion) sb.append("delins").append(formatAminoAcidCode(type, (AminoAcidCode[]) mutation.getMutation().getValue()));
        else if (mutation.getMutation() instanceof Frameshift) sb.append("fs").append(formatAminoAcidCode(type, AminoAcidCode.Stop)).append(mutation.getMutation().getValue());

        return sb.toString();
    }

    private String formatAminoAcidCode(AACodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            if (type == AACodeType.ONE_LETTER) sb.append(String.valueOf(aa.get1LetterCode()));
            else sb.append(String.valueOf(aa.get3LetterCode()));
        }

        return sb.toString();
    }

    @Override
    public ProteinMutation parse(String source) throws ParseException {

        Preconditions.checkNotNull(source);
        Preconditions.checkArgument(source.startsWith("p."), "not a valid protein sequence variant");

        ProteinMutation.FluentBuilder builder = new ProteinMutation.FluentBuilder();

        ProteinMutation mutation = buildSubstitution(source, builder);
        // TODO: I would have preferred to find an elegant way to do the following...
        if (mutation == null) mutation = buildDeletion(source, builder);
        if (mutation == null) mutation = buildFrameshift(source, builder);
        if (mutation == null) mutation = buildDeletionInsertion(source, builder);

        if (mutation == null) throw new ParseException("not a valid protein mutation", 0);

        return mutation;
    }

    private AminoAcidCode valueOfAminoAcidCode(String code1, String code2and3) {

        if (code2and3 == null) return AminoAcidCode.valueOfCode(code1);

        return AminoAcidCode.valueOfCode(code1+code2and3);
    }

    private ProteinMutation buildSubstitution(String source, ProteinMutation.FluentBuilder builder) {

        Matcher m = SUBSTITUTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode substitutedAA = valueOfAminoAcidCode(m.group(4), m.group(5));

            return builder.aminoAcid(affectedAA, affectedAAPos).substitutedBy(substitutedAA).build();
        }

        return null;
    }

    private ProteinMutation buildDeletion(String source, ProteinMutation.FluentBuilder builder) {

        Matcher m = DELETION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {

                return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst).deleted().build();
            }

            AminoAcidCode affectedAALast = valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast).deleted().build();
        }

        return null;
    }

    private ProteinMutation buildFrameshift(String source, ProteinMutation.FluentBuilder builder) {

        Matcher m = FRAMESHIFT_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            return builder.aminoAcid(affectedAA, affectedAAPos).thenFrameshift(Integer.parseInt(m.group(4))).build();
        }

        return null;
    }

    private ProteinMutation buildDeletionInsertion(String source, ProteinMutation.FluentBuilder builder) {

        Matcher m = DELETION_INSERTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfCodeSequence(m.group(7));

            if (m.group(4) == null) return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst)
                    .deletedAndInserts(insertedAAs).build();

            AminoAcidCode affectedAALast = valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .deletedAndInserts(insertedAAs).build();
        }

        return null;
    }

    /**
     * Parse string provides by COSMIC
     * @param source
     * @return
     */
    public ProteinMutation parseNonStandardCosmic(String source) {

        return null;
    }

    static String asHGVMutationFormat(String value) {

        Preconditions.checkArgument(value.startsWith("p."));

        Pattern pat = Pattern.compile("([A-Z])\\d+_?([A-Z])?.*");

        Matcher matcher = pat.matcher(value);

        while (matcher.find()) {

            System.out.println(matcher.group(1)+" AND "+matcher.group(2));
        }

        // 1. replace all 1-letter AAs by 3-letter code
        // 2. replace '*' by 'Ter'
        // 3. remove everything after del
        // 4. replace 'fs>\d+' by 'fs\d+'
        // 4. replace '>' by 'delins'

        return value;
    }
}
