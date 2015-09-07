package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.mutation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fnikitin on 07/09/15.
 */
public class FrameshiftHGVFormat implements MutationEffectFormat<Frameshift> {

    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)(\\d+)$");
    private static final Pattern FRAMESHIFT_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)>?(\\d+)$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, AbstractProteinMutationFormat.ParsingMode mode) {

        Matcher m = (mode == AbstractProteinMutationFormat.ParsingMode.STRICT) ? FRAMESHIFT_PATTERN.matcher(source) : FRAMESHIFT_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AbstractProteinMutationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            return builder.aminoAcid(affectedAA, affectedAAPos).thenFrameshift(Integer.parseInt(m.group(4))).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Frameshift mutation, ProteinMutationFormat.AACodeType type) {

        sb.append("fs").append(AbstractProteinMutationFormat.formatAminoAcidCode(type, AminoAcidCode.Stop)).append(mutation.getValue());

    }
}
