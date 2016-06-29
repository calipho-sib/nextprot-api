package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameshiftHGVSFormat implements ProteinSequenceChangeFormat<Frameshift> {

    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)([A-Z])([a-z]{2})?fs(?:\\*|Ter)(\\d+)$");
    //private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)(\\d+)$");
    //private static final Pattern FRAMESHIFT_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)>?(\\d+)$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, AbstractProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = FRAMESHIFT_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AbstractProteinSequenceVariationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode newAA = AbstractProteinSequenceVariationFormat.valueOfAminoAcidCode(m.group(4), m.group(5));

            return builder.aminoAcid(affectedAA, affectedAAPos).thenFrameshift(newAA, Integer.parseInt(m.group(6))).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, AbstractProteinSequenceVariationFormat.ParsingMode mode) {
        return source.matches(FRAMESHIFT_PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Frameshift change, ProteinSequenceVariationFormat.AACodeType type) {

        sb
                .append(AbstractProteinSequenceVariationFormat.formatAminoAcidCode(type, change.getValue().getChangedAminoAcid()))
                .append("fs")
                .append(AbstractProteinSequenceVariationFormat.formatAminoAcidCode(type, AminoAcidCode.Stop))
                .append(change.getValue().getNewTerminationPosition());
    }
}
