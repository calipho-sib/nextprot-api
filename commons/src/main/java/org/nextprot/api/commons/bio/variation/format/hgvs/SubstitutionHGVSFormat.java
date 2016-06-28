package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nextprot.api.commons.bio.variation.format.hgvs.ProteinSequenceVariationHGVSFormat.formatAminoAcidCode;
import static org.nextprot.api.commons.bio.variation.format.hgvs.ProteinSequenceVariationHGVSFormat.valueOfAminoAcidCode;


public class SubstitutionHGVSFormat implements ProteinSequenceChangeFormat<Substitution> {

    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("^p\\.([A-Z*])([a-z]{2})?(\\d+)([A-Z*])([a-z]{2})?$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder,
                                                  AbstractProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

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
    public void format(StringBuilder sb, Substitution change, ProteinSequenceVariationFormat.AACodeType type) {

        sb.append(formatAminoAcidCode(type, change.getValue()));
    }
}
