package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.TerminationExtension;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Specifications: http://varnomen.hgvs.org/recommendations/protein/variant/extension/
 */
public class TerminationExtensionHGVSFormat implements SequenceChangeFormat<TerminationExtension> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.(?:Ter|\\*)(\\d+)((?:[A-Z])(?:[a-z]{2})?)ext(?:Ter|\\*)(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariationFormat.ParsingMode mode) throws ParseException {

        Matcher m =  PATTERN.matcher(source);

        if (m.matches()) {

            int affectedStopPos = Integer.parseInt(m.group(1));

            AminoAcidCode newAminoAcid = AminoAcidCode.parseAminoAcidCode(m.group(2));

            int newDownstreamPos = Integer.parseInt(m.group(3));

            return builder.selectAminoAcid(AminoAcidCode.STOP, affectedStopPos).thenTerminationExtension(newDownstreamPos, newAminoAcid).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariationFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, TerminationExtension change, AminoAcidCode.CodeType type) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()))
                .append("ext")
                .append(AminoAcidCode.formatAminoAcidCode(type, AminoAcidCode.STOP))
                .append(change.getNewDownstreamTermPos());
    }
}
