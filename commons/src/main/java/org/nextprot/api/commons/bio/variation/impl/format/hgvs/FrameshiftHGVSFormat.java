package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.Frameshift;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameshiftHGVSFormat implements SequenceChangeFormat<Frameshift> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)([A-Z])([a-z]{2})?fs(?:\\*|Ter)(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode newAA = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));

            int shift = Integer.parseInt(m.group(6));

            if (shift <= 1)
                throw new ParseException("the description of a frame shift variant can not contain " +
                        "“fsTer1”, such a variant is a nonsense variant (see Substitution). The shortest frame shift variant " +
                        "possible contains 'fsTer2' (see http://varnomen.hgvs.org/recommendations/protein/variant/frameshift/)", 0);

            return builder.selectAminoAcid(affectedAA, affectedAAPos).thenFrameshift(newAA, shift).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Frameshift change, AminoAcidCode.CodeType type) {

        sb
                .append(AminoAcidCode.formatAminoAcidCode(type, change.getValue().getChangedAminoAcid()))
                .append("fs")
                .append(AminoAcidCode.formatAminoAcidCode(type, AminoAcidCode.STOP))
                .append(change.getValue().getNewTerminationPosition());
    }
}
