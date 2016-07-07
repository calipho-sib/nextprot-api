package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.Deletion;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.format.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.seq.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionHGVSFormat implements SequenceChangeFormat<Deletion> {

    private static final Pattern DELETION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del$");
    private static final Pattern DELETION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del.*$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariation.FluentBuilder builder, ProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = (mode == ProteinSequenceVariationHGVSFormat.ParsingMode.STRICT) ? DELETION_PATTERN.matcher(source) : DELETION_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {

                return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst).deletes().build();
            }

            AminoAcidCode affectedAALast = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast).deletes().build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ProteinSequenceVariationFormat.ParsingMode mode) {
        return (mode == ProteinSequenceVariationHGVSFormat.ParsingMode.STRICT) ? source.matches(DELETION_PATTERN.pattern()) : source.matches(DELETION_PATTERN_PERMISSIVE.pattern());
    }

    @Override
    public void format(StringBuilder sb, Deletion change, AminoAcidCode.AACodeType type) {

        sb.append("del");
    }
}
