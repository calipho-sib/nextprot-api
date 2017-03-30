package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChangeHGVSFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.impl.ExtensionInitiation;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Specifications: http://varnomen.hgvs.org/recommendations/protein/variant/extension/
 */
public class ExtensionInitiationHGVSFormat implements SequenceChangeHGVSFormat<ExtensionInitiation> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.M(?:et)?1((?:[A-Z])(?:[a-z]{2})?)?ext(-\\d+)$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder, SequenceVariantHGVSFormat.ParsingMode mode) throws ParseException {

        Matcher m =  PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode newAminoAcid = AminoAcidCode.METHIONINE;

            if (m.group(1) != null) {
                newAminoAcid = AminoAcidCode.parseAminoAcidCode(m.group(1));
            }

            int newUpstreamPos = Integer.parseInt(m.group(2));

            return builder.selectAminoAcid(AminoAcidCode.METHIONINE, 1).thenInitiationExtension(newUpstreamPos, newAminoAcid).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, SequenceVariantHGVSFormat.ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, ExtensionInitiation change, AminoAcidCode.CodeType type) {

        if (change.getValue() != AminoAcidCode.METHIONINE) {

            sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
        }

        sb.append("ext").append(change.getNewPos());
    }
}
