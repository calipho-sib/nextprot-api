package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.mutation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat.formatAminoAcidCode;
import static org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat.valueOfAminoAcidCode;

/**
 * Created by fnikitin on 07/09/15.
 */
public class SubstitutionHGVFormat implements MutationEffectFormat<Substitution> {

    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("^p\\.([A-Z*])([a-z]{2})?(\\d+)([A-Z*])([a-z]{2})?$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, AbstractProteinMutationFormat.ParsingMode mode) {

        Matcher m = SUBSTITUTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode substitutedAA = valueOfAminoAcidCode(m.group(4), m.group(5));

            return builder.aminoAcid(affectedAA, affectedAAPos).substitutedBy(substitutedAA).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Substitution mutation, ProteinMutationFormat.AACodeType type) {

        sb.append(formatAminoAcidCode(type, mutation.getValue()));
    }
}
