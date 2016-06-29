package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Specifications: http://varnomen.hgvs.org/recommendations/protein/variant/insertion/
 */
public class InsertionHGVSFormat implements ProteinSequenceChangeFormat<Insertion> {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)_([A-Z])([a-z]{2})?(\\d+)ins((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public ProteinSequenceVariation parseWithMode(String source, ProteinSequenceVariation.FluentBuilder builder, AbstractProteinSequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m =  INSERTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AbstractProteinSequenceVariationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode affectedAALast = AbstractProteinSequenceVariationFormat.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            if (affectedAAPosLast != (affectedAAPosFirst+1)) {
                throw new ParseException("should contain two flanking residues, e.g. Lys23 and Leu24", 0);
            }

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfOneLetterCodeSequence(m.group(7));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .inserts(insertedAAs).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Insertion change, ProteinSequenceVariationFormat.AACodeType type) {

        sb.append("ins").append(AbstractProteinSequenceVariationFormat.formatAminoAcidCode(type, change.getValue()));
    }
}
