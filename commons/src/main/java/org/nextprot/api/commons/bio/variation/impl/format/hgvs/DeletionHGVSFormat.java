package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.Deletion;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionHGVSFormat implements SequenceChangeFormat<Deletion> {

    private static final Pattern DELETION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del$");
    private static final Pattern DELETION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del.*$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = (mode == SequenceVariationHGVSFormat.ParsingMode.STRICT) ? DELETION_PATTERN.matcher(source) : DELETION_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {

                return builder.selectAminoAcid(affectedAAFirst, affectedAAPosFirst).thenDelete().build();
            }

            AminoAcidCode affectedAALast = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.selectAminoAcidRange(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast).thenDelete().build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return (mode == SequenceVariationHGVSFormat.ParsingMode.STRICT) ? source.matches(DELETION_PATTERN.pattern()) : source.matches(DELETION_PATTERN_PERMISSIVE.pattern());
    }

    @Override
    public void format(StringBuilder sb, Deletion change, AminoAcidCode.AACodeType type) {

        sb.append("del");
    }
}
