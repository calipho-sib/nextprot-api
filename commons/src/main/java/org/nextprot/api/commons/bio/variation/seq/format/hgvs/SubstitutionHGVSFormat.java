package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.Substitution;
import org.nextprot.api.commons.bio.variation.seq.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.seq.format.ProteinSequenceChangeFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubstitutionHGVSFormat implements ProteinSequenceChangeFormat<Substitution> {

    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("^p\\.([A-Z*])([a-z]{2})?(\\d+)([A-Z*])([a-z]{2})?$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder,
                                                  AbstractProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = SUBSTITUTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode substitutedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));

            return builder.aminoAcid(affectedAA, affectedAAPos).substitutedBy(substitutedAA).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, AbstractProteinSequenceVariationFormat.ParsingMode mode) {
        return source.matches(SUBSTITUTION_PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Substitution change, AminoAcidCode.AACodeType type) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
