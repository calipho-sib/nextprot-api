package org.nextprot.api.commons.bio.variation.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class AminoAcidModificationBedFormat extends SequenceVariationFormat {

    private final ChangingAAsFormat changingAAsFormat;
    private final Map<SequenceChange.Type, SequenceChangeFormat> changeFormats;

    public AminoAcidModificationBedFormat() {

        changingAAsFormat = new BEDFormat();
        changeFormats = new EnumMap<>(SequenceChange.Type.class);
        changeFormats.put(SequenceChange.Type.PTM, new SingleModificationBEDFormat());
    }

    @Override
    protected ChangingAAsFormat getChangingAAsFormat() {

        return changingAAsFormat;
    }

    @Override
    protected SequenceChangeFormat getChangeFormat(SequenceChange.Type changeType) {

        return changeFormats.get(changeType);
    }

    @Override
    protected Collection<SequenceChange.Type> getAvailableChangeTypes() {

        return changeFormats.keySet();
    }

    @Override
    public String format(SequenceVariation variation, AminoAcidCode.CodeType type) {

        StringBuilder sb = new StringBuilder();

        changeFormats.get(variation.getSequenceChange().getType()).format(sb, variation.getSequenceChange(), type);

        changingAAsFormat.format(sb, variation, type);

        return sb.toString();
    }
}
