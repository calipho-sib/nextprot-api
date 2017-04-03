package org.nextprot.api.commons.bio.variation.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ParsingMode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.impl.seqchange.Duplication;
import org.nextprot.api.commons.bio.variation.seqchange.SequenceChangeHGVSFormat;
import org.nextprot.api.commons.utils.StringUtils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Format: “prefix”“amino_acid(s)+position(s)_deleted”“dup”, e.g. p.Cys76_Glu79dup
 *
 * http://varnomen.hgvs.org/recommendations/protein/variant/duplication/
 */
public class DuplicationHGVSFormat implements SequenceChangeHGVSFormat<Duplication> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?dup$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(1), m.group(2)));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {

                return builder.selectAminoAcid(affectedAAFirst, affectedAAPosFirst).thenDuplicate().build();
            }

            AminoAcidCode affectedAALast = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(4), m.group(5)));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.selectAminoAcidRange(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .thenDuplicate().build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Duplication change, AminoAcidCode.CodeType type) {

        sb.append("dup");
    }
}
