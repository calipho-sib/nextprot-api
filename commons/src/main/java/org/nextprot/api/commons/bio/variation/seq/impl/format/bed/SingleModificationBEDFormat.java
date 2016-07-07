package org.nextprot.api.commons.bio.variation.seq.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.impl.AminoAcidModification;
import org.nextprot.api.commons.bio.variation.seq.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.seq.SequenceChangeFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * P-Tyr223       one mod
 * P-Ser21-Ser115 two mods
 */
public class SingleModificationBEDFormat implements SequenceChangeFormat<AminoAcidModification> {

    // MOD-AApos
    private static final Pattern PATTERN = Pattern.compile("^(\\w+)-([A-Z*])([a-z]{2})?(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationImpl.FluentBuilder builder,
                                           SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidModification aaChange = AminoAcidModification.valueOfAminoAcidModification(m.group(1));
            AminoAcidCode affectedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(2), m.group(3));
            int affectedAAPos = Integer.parseInt(m.group(4));

            return builder.aminoAcid(affectedAA, affectedAAPos).modifies(aaChange).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, AminoAcidModification change, AminoAcidCode.AACodeType type) {

        sb
                .append(change.getValue().getName())
                .append("-");
    }
}
