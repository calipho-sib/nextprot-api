package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.impl.DeletionAndInsertion;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionInsertionHGVSFormat implements SequenceChangeFormat<DeletionAndInsertion> {

    private static final Pattern DELETION_INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?delins((?:[A-Z\\*]([a-z]{2})?)+)$");
    private static final Pattern DELETION_INSERTION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?(?:delins|>)((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = (mode == SequenceVariationFormat.ParsingMode.STRICT) ? DELETION_INSERTION_PATTERN.matcher(source) : DELETION_INSERTION_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfOneLetterCodeSequence(m.group(7));

            try {
                if (m.group(4) == null) return builder.selectAminoAcid(affectedAAFirst, affectedAAPosFirst)
                        .thenDeleteAndInsert(insertedAAs).build();

                AminoAcidCode affectedAALast = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));
                int affectedAAPosLast = Integer.parseInt(m.group(6));

                return builder.selectAminoAcidRange(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                        .thenDeleteAndInsert(insertedAAs).build();
            } catch (BuildException e) {

                throw new ParseException(e.getMessage(), 0);
            }
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return (mode == SequenceVariationFormat.ParsingMode.STRICT) ? source.matches(DELETION_INSERTION_PATTERN.pattern()) : source.matches(DELETION_INSERTION_PATTERN_PERMISSIVE.pattern());
    }

    @Override
    public void format(StringBuilder sb, DeletionAndInsertion change, AminoAcidCode.AACodeType type) {

        sb.append("delins").append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
