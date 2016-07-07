package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.Duplication;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.format.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.seq.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Format: “prefix”“amino_acid(s)+position(s)_deleted”“dup”, e.g. p.Cys76_Glu79dup
 *
 * http://varnomen.hgvs.org/recommendations/protein/variant/duplication/
 */
public class DuplicationHGVSFormat implements SequenceChangeFormat<Duplication> {

    private static final Pattern DUPLICATION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?dup$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariation.FluentBuilder builder, ProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m = DUPLICATION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {

                return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst).duplicates().build();
            }

            AminoAcidCode affectedAALast = AminoAcidCode.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .duplicates().build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ProteinSequenceVariationFormat.ParsingMode mode) {
        return source.matches(DUPLICATION_PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Duplication change, AminoAcidCode.AACodeType type) {

        sb.append("dup");
    }
}
