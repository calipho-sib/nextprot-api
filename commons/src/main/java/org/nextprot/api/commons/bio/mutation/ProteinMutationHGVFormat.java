package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

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
    public ProteinMutation parse(String source) {
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
