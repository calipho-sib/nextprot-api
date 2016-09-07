package org.nextprot.api.commons.bio.variation.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.impl.AminoAcidModification;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse single PTM with the format:
 * MOD-AApos (example: P-Tyr223 represents a phosphorylation of tyrosine at position 223)
 */
public class SingleModificationBEDFormat implements SequenceChangeFormat<AminoAcidModification> {

    private static final Pattern PATTERN = Pattern.compile("^(\\w+)-([A-Z])([a-z]{2})?(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder,
                                           SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidModification aaChange = AminoAcidModification.valueOfAminoAcidModification(m.group(1));
            AminoAcidCode affectedAA = AminoAcidCode.parseAminoAcidCode(m.group(2) + ((m.group(3) != null) ? m.group(3) : ""));
            int affectedAAPos = Integer.parseInt(m.group(4));

            return builder.selectAminoAcid(affectedAA, affectedAAPos).thenAddModification(aaChange).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, AminoAcidModification change, AminoAcidCode.CodeType type) {

        sb
                .append(change.getName())
                .append("-");
    }
}
