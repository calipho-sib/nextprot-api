package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.PTM;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeFormat;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse single glycosylation with the format:
 *
 * PTM-id_pos (example: PTM-0253_21 represents a glycosylation at position 21)
 */
public class SinglePtmBioEditorFormat implements SequenceChangeFormat<SequenceVariationBuilder.StartBuildingFromAAs, PTM> {

    private static final Pattern PATTERN = Pattern.compile("^(PTM-\\d{4})_(\\d+)$");

    @Override
    public SequenceVariation parse(String source, SequenceVariationBuilder.StartBuildingFromAAs builder) throws ParseException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            PTM aaChange = new PTM(m.group(1));
            int affectedAAPos = Integer.parseInt(m.group(2));

            return builder.selectAminoAcid(affectedAAPos).thenAddModification(aaChange).build();
        }

        return null;
    }

    @Override
    public boolean matches(String source) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, PTM change, AminoAcidCode.CodeType type) {

        sb
                .append(change.getValue())
                .append("_");
    }
}
