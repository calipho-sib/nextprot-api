package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.DeletionAndInsertion;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionInsertionHGVSFormat implements ProteinSequenceChangeFormat<DeletionAndInsertion> {

    private static final Pattern DELETION_INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?delins((?:[A-Z\\*]([a-z]{2})?)+)$");
    private static final Pattern DELETION_INSERTION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?(?:delins|>)((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, ProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = (mode == ProteinSequenceVariationFormat.ParsingMode.STRICT) ? DELETION_INSERTION_PATTERN.matcher(source) : DELETION_INSERTION_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfOneLetterCodeSequence(m.group(7));

            if (m.group(4) == null) return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst)
                    .deletedAndInserts(insertedAAs).build();

            AminoAcidCode affectedAALast = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .deletedAndInserts(insertedAAs).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ProteinSequenceVariationFormat.ParsingMode mode) {
        return (mode == ProteinSequenceVariationFormat.ParsingMode.STRICT) ? source.matches(DELETION_INSERTION_PATTERN.pattern()) : source.matches(DELETION_INSERTION_PATTERN_PERMISSIVE.pattern());
    }

    @Override
    public void format(StringBuilder sb, DeletionAndInsertion change, AminoAcidCode.AACodeType type) {

        sb.append("delins").append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
