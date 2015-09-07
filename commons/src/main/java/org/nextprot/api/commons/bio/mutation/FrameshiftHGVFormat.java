package org.nextprot.api.commons.bio.mutation;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nextprot.api.commons.bio.mutation.ProteinMutationHGVFormat.formatAminoAcidCode;
import static org.nextprot.api.commons.bio.mutation.ProteinMutationHGVFormat.valueOfAminoAcidCode;

/**
 * Created by fnikitin on 07/09/15.
 */
public class FrameshiftHGVFormat implements MutationEffectFormat<Frameshift> {

    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)(\\d+)$");
    private static final Pattern FRAMESHIFT_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)>?(\\d+)$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, ProteinMutationHGVFormat.ParsingMode mode) {

        Matcher m = (mode == ProteinMutationHGVFormat.ParsingMode.STRICT) ? FRAMESHIFT_PATTERN.matcher(source) : FRAMESHIFT_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            return builder.aminoAcid(affectedAA, affectedAAPos).thenFrameshift(Integer.parseInt(m.group(4))).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Frameshift mutation, ProteinMutationFormat.AACodeType type) {

        sb.append("fs").append(formatAminoAcidCode(type, AminoAcidCode.Stop)).append(mutation.getValue());

    }
}
