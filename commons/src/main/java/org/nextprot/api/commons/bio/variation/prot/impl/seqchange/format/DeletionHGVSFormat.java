package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Deletion;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;
import org.nextprot.api.commons.utils.StringUtils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionHGVSFormat implements SequenceChangeHGVSFormat<Deletion> {

    private static final Pattern PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del(?!ins)$");
    private static final Pattern PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?del(?!ins).*$");

    @Override
    public SequenceVariation parseWithMode(String source, SequenceVariationBuilder.StartBuilding builder, ParsingMode mode) throws ParseException {

        Matcher m = (mode == ParsingMode.STRICT) ? PATTERN.matcher(source) : PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(1), m.group(2)));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            if (m.group(4) == null) {
                return builder.selectAminoAcid(affectedAAFirst, affectedAAPosFirst).thenDelete().build();
            }

            AminoAcidCode affectedAALast = AminoAcidCode.parseAminoAcidCode(StringUtils.concat(m.group(4), m.group(5)));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.selectAminoAcidRange(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast).thenDelete().build();
        }

        return null;
    }

    @Override
    public boolean matchesWithMode(String source, ParsingMode mode) {
        return (mode == ParsingMode.STRICT) ? source.matches(PATTERN.pattern()) : source.matches(PATTERN_PERMISSIVE.pattern());
    }

    @Override
    public void format(StringBuilder sb, Deletion change, AminoAcidCode.CodeType type) {

        sb.append("del");
    }
}
