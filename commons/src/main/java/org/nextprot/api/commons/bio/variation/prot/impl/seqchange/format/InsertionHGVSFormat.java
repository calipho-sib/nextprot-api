package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Insertion;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;
import org.nextprot.api.commons.utils.StringUtils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Specifications: http://varnomen.hgvs.org/recommendations/protein/variant/insertion/
 */
public class InsertionHGVSFormat implements SequenceChangeHGVSFormat<Insertion> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)_([A-Z])([a-z]{2})?(\\d+)ins((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.StartBuilding builder, ParsingMode mode) throws ParseException {

        Matcher m =  PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(1), m.group(2)));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode affectedAALast = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(4), m.group(5)));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            if (affectedAAPosLast != (affectedAAPosFirst+1)) {
                throw new ParseException("should contain two flanking residues, e.g. Lys23 and Leu24", 0);
            }

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfAminoAcidCodeSequence(m.group(7));

            return builder.selectAminoAcidRange(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .thenInsert(insertedAAs).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Insertion change, AminoAcidCode.CodeType type) {

        sb.append("ins").append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
