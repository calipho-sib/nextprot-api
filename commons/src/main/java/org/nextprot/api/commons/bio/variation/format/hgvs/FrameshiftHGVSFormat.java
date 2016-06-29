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

    private static final Pattern FRAMESHIFT_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)(\\d+)$");
    private static final Pattern FRAMESHIFT_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)fs(?:\\*|Ter)>?(\\d+)$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, AbstractProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = (mode == AbstractProteinSequenceVariationFormat.ParsingMode.STRICT) ? FRAMESHIFT_PATTERN.matcher(source) : FRAMESHIFT_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AbstractProteinSequenceVariationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPos = Integer.parseInt(m.group(3));

            return builder.aminoAcid(affectedAA, affectedAAPos).thenFrameshift(Integer.parseInt(m.group(4))).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Frameshift change, ProteinSequenceVariationFormat.AACodeType type) {

        sb.append("fs").append(AbstractProteinSequenceVariationFormat.formatAminoAcidCode(type, AminoAcidCode.Stop)).append(change.getValue());

    }
}
