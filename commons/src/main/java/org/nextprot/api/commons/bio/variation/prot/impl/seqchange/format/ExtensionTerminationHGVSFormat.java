package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.ExtensionTermination;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Specifications: http://varnomen.hgvs.org/recommendations/protein/variant/extension/
 */
public class ExtensionTerminationHGVSFormat implements SequenceChangeHGVSFormat<ExtensionTermination> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.(?:Ter|\\*)(\\d+)((?:[A-Z])(?:[a-z]{2})?)ext\\*(\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.StartBuilding builder, ParsingMode mode) throws ParseException {

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
    public boolean matchesWithMode(String source, ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, ExtensionTermination change, AminoAcidCode.CodeType type) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()))
                .append("ext*")
                .append(change.getNewPos());
    }
}
