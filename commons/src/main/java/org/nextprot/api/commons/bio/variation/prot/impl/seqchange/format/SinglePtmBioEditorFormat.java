package org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.*;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.UniProtPTM;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse single glycosylation with the format:
 *
 * PTM-id_pos (example: PTM-0253_21 represents a glycosylation at position 21)
 */
public class SinglePtmBioEditorFormat implements SequenceChangeFormat<SequenceVariationBuilder.StartBuildingFromAAs, UniProtPTM> {

    private static final Pattern PATTERN = Pattern.compile("^(PTM-\\d{4})_(\\d+)$");

    @Override
    public SequenceVariation parse(String source, SequenceVariationBuilder.StartBuildingFromAAs builder) throws SequenceVariationBuildException {

        Matcher m = PATTERN.matcher(source);

        if (m.matches()) {

            UniProtPTM aaChange = new UniProtPTM(m.group(1));
            int affectedAAPos = Integer.parseInt(m.group(2));

            try {
                return builder.selectAminoAcid(affectedAAPos).thenAddModification(aaChange).build();
            } catch (VariationOutOfSequenceBoundException e) {

                throw new SequenceVariationBuildException(e);
            }
        }

        return null;
    }

    @Override
    public boolean matches(String source) {
        return source.matches(PATTERN.pattern());
    }

    @Override
    public void format(StringBuilder sb, UniProtPTM change, AminoAcidCode.CodeType type) {

        sb
                .append(change.getValue())
                .append("_");
    }
}
