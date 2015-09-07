package org.nextprot.api.commons.bio.mutation;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nextprot.api.commons.bio.mutation.ProteinMutationHGVFormat.valueOfAminoAcidCode;

/**
 * Created by fnikitin on 07/09/15.
 */
public class DeletionHGVFormat implements MutationEffectFormat<Deletion> {

    private static final Pattern DELETION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del$");
    private static final Pattern DELETION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del.*$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, ProteinMutationHGVFormat.ParsingMode mode) {

        Matcher m = (mode == ProteinMutationHGVFormat.ParsingMode.STRICT) ? DELETION_PATTERN.matcher(source) : DELETION_PATTERN_PERMISSIVE.matcher(source);

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

    @Override
    public void format(StringBuilder sb, Deletion mutation, ProteinMutationFormat.AACodeType type) {

        sb.append("del");
    }
}
