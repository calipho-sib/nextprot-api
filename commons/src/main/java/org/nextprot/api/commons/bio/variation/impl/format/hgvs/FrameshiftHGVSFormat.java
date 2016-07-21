package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.impl.Frameshift;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameshiftHGVSFormat implements SequenceChangeFormat<Frameshift> {

    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)([A-Z])([a-z]{2})?fs(?:\\*|Ter)(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = FRAMESHIFT_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode newAA = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));

            try {
                return builder.selectAminoAcid(affectedAA, affectedAAPos).thenFrameshift(newAA, Integer.parseInt(m.group(6))).build();
            } catch (BuildException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(FRAMESHIFT_PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Frameshift change, AminoAcidCode.AACodeType type) {

        sb
                .append(AminoAcidCode.formatAminoAcidCode(type, change.getValue().getChangedAminoAcid()))
                .append("fs")
                .append(AminoAcidCode.formatAminoAcidCode(type, AminoAcidCode.STOP))
                .append(change.getValue().getNewTerminationPosition());
    }
}
