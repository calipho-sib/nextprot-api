package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Format and parse SequenceVariation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class SequenceVariantHGVSFormat extends SequenceVariationFormat {

    private final SequenceVariantHGVSFormatter sequenceVariantFormatter;
    private final Map<SequenceChange.Type, SequenceChangeFormat> changeFormats;

    public SequenceVariantHGVSFormat() {

        sequenceVariantFormatter = new SequenceVariantHGVSFormatter();
        changeFormats = new EnumMap<>(SequenceChange.Type.class);
        changeFormats.put(SequenceChange.Type.INSERTION, new InsertionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DUPLICATION, new DuplicationHGVSFormat());
        changeFormats.put(SequenceChange.Type.SUBSTITUTION, new SubstitutionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DELETION, new DeletionHGVSFormat());
        changeFormats.put(SequenceChange.Type.DELETION_INSERTION, new DeletionInsertionHGVSFormat());
        changeFormats.put(SequenceChange.Type.FRAMESHIFT, new FrameshiftHGVSFormat());
        changeFormats.put(SequenceChange.Type.EXTENSION_INIT, new ExtensionInitiationHGVSFormat());
        changeFormats.put(SequenceChange.Type.EXTENSION_TERM, new ExtensionTerminationHGVSFormat());
    }

    @Override
    protected String prefixFormatter() {
        // protein sequence variation
        return "p.";
    }

    @Override
    protected SequenceVariantHGVSFormatter getChangingSequenceFormatter() {

        return sequenceVariantFormatter;
    }

    @Override
    protected SequenceChangeFormat getSequenceChangeFormat(SequenceChange.Type changeType) {

        return changeFormats.get(changeType);
    }

    @Override
    protected Collection<SequenceChange.Type> getAvailableChangeTypes() {

        return changeFormats.keySet();
    }
}
