package org.nextprot.api.commons.bio.variation.prot.impl.format;

import com.google.common.collect.Lists;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.UniProtPTM;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format.SinglePtmBioEditorFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.varseq.format.AminoAcidModificationBEDFormatter;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeFormat;

import java.text.ParseException;
import java.util.Collection;
import java.util.Objects;

public class SequencePtmBioEditorFormat extends SequenceVariationFormat {

    private final AminoAcidModificationBEDFormatter aminoAcidModificationFormatter;
    private final SinglePtmBioEditorFormat ptmFormat;

    public SequencePtmBioEditorFormat() {

        aminoAcidModificationFormatter = new AminoAcidModificationBEDFormatter();
        ptmFormat = new SinglePtmBioEditorFormat();
    }

    @Override
    protected AminoAcidModificationBEDFormatter getChangingSequenceFormatter() {

        return aminoAcidModificationFormatter;
    }

    @Override
    protected SequenceChangeFormat getSequenceChangeFormat(SequenceChange.Type changeType) {

        return ptmFormat;
    }

    @Override
    protected Collection<SequenceChange.Type> getAvailableChangeTypes() {

        return Lists.newArrayList(SequenceChange.Type.PTM);
    }

    @Override
    public String format(SequenceVariation variation, AminoAcidCode.CodeType type) {

        StringBuilder sb = new StringBuilder();

        if (variation.getSequenceChange() instanceof UniProtPTM) {

            ptmFormat.format(sb, (UniProtPTM) variation.getSequenceChange(), type);
            aminoAcidModificationFormatter.format(variation, type, sb);

            return sb.toString();
        }

        throw new IllegalArgumentException("Not a glycosylation: cannot format variation "+variation.getSequenceChange());
    }

    public SequenceVariation parse(String source, String aas) throws ParseException {

        Objects.requireNonNull(source);

        return parse(source, new SequenceVariationImpl.StartBuilding().fromAAs(aas));
    }
}
