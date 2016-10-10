package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.variation.ChangingAAsFormat;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * <code>ProteinMutationHGVFormat</code> can format and parse
 * ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class SequenceVariationHGVSFormat extends SequenceVariationFormat {

    private final ChangingAAsFormat changingAAsFormat;
    private final Map<SequenceChange.Type, SequenceChangeFormat> changeFormats;

    public SequenceVariationHGVSFormat() {

        changingAAsFormat = new HGVSFormat();
        changeFormats = new EnumMap<>(SequenceChange.Type.class);
        changeFormats.put(SequenceChange.Type.INSERTION, new InsertionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DUPLICATION, new DuplicationHGVSFormat());
        changeFormats.put(SequenceChange.Type.SUBSTITUTION, new SubstitutionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DELETION, new DeletionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DELETION_INSERTION, new DeletionInsertionHGVSFormat());
        changeFormats.put(SequenceChange.Type.FRAMESHIFT, new FrameshiftHGVSFormat());
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
}
