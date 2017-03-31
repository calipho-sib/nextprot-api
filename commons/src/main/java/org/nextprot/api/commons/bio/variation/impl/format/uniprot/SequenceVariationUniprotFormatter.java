package org.nextprot.api.commons.bio.variation.impl.format.uniprot;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by fnikitin on 29.03.17.
 */
public class SequenceVariationUniprotFormatter implements SequenceVariationFormatter<Map<String, String>> {

    private final String originalSequence;

    private final String CHANGING_AAS = "changing-aas";

    public SequenceVariationUniprotFormatter(String originalSequence) {

        this.originalSequence = originalSequence;
    }

    @Override
    public Map<String, String> format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type) {

        HashMap<String, String> map = new HashMap<>();

        int firstIndex = sequenceVariation.getChangingSequence().getFirstAminoAcidPos()-1;
        int lastIndex = sequenceVariation.getChangingSequence().getLastAminoAcidPos();

        map.put(CHANGING_AAS, originalSequence.substring(firstIndex, lastIndex));

        //formatter.format(sb, sequenceVariation.getSequenceChange(), AminoAcidCode.CodeType.ONE_LETTER);

        return map;
    }
}
