package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format.*;
import org.nextprot.api.commons.bio.variation.prot.impl.varseq.format.SequenceVariantHGVSFormatter;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;

import java.text.ParseException;
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
    private final Map<SequenceChange.Type, SequenceChangeHGVSFormat> changeFormats;
    private final ParsingMode parsingMode;

    public SequenceVariantHGVSFormat() {

        this(ParsingMode.STRICT);
    }

    public SequenceVariantHGVSFormat(ParsingMode parsingMode) {

        this.parsingMode = parsingMode;

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
    protected SequenceChangeHGVSFormat getSequenceChangeFormat(SequenceChange.Type changeType) {

        return changeFormats.get(changeType);
    }

    @Override
    protected Collection<SequenceChange.Type> getAvailableChangeTypes() {

        return changeFormats.keySet();
    }

    @Override
    public SequenceVariation parse(String source, SequenceVariationBuilder.Start builder) throws ParseException {

        for (SequenceChange.Type changeType : getAvailableChangeTypes()) {

            SequenceChangeHGVSFormat format = getSequenceChangeFormat(changeType);

            if (format.matchesWithMode(source, parsingMode))
                return format.parseWithMode(source, (SequenceVariationBuilder.StartBuilding) builder, parsingMode);
        }

        throw new ParseException(source + ": not a valid protein sequence variant", 0);
    }
}
