package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.impl.Substitution;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubstitutionHGVSFormat implements SequenceChangeFormat<Substitution> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z*])([a-z]{2})?(\\d+)([A-Z*])([a-z]{2})?$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder,
                                           SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode substitutedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));

            return builder.selectAminoAcid(affectedAA, affectedAAPos).thenSubstituteWith(substitutedAA).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Substitution change, AminoAcidCode.AACodeType type) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
