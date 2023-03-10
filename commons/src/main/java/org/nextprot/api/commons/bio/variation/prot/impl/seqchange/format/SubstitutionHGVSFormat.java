package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Substitution;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;
import org.nextprot.api.commons.utils.StringUtils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubstitutionHGVSFormat implements SequenceChangeHGVSFormat<Substitution> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z*])([a-z]{2})?(\\d+)([A-Z*])([a-z]{2})?$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.StartBuilding builder,
                                           ParsingMode mode) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAA = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(1), m.group(2)));
            if (AminoAcidCode.STOP.equals(affectedAA)) {
                throw new ParseException("should not contain STOP codon as affected amino acid (e.g. p.Ter23Ser is not allowed), it should be an extension (e.g. p.*23Serext*13)", 0);
            }
            int affectedAAPos = Integer.parseInt(m.group(3));

            AminoAcidCode substitutedAA = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(4), m.group(5)));

            return builder.selectAminoAcid(affectedAA, affectedAAPos).thenSubstituteWith(substitutedAA).build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ParsingMode mode) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, Substitution change, AminoAcidCode.CodeType type) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, change.getValue()));
    }
}
