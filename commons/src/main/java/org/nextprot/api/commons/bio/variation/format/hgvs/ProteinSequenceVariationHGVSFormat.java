package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.ChangingAAsFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceChangeFormat;

/**
 * <code>ProteinMutationHGVFormat</code> can format and parse
 * ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class ProteinSequenceVariationHGVSFormat extends AbstractProteinSequenceVariationFormat {

    private final ChangingAAsFormat changingAAsFormat = new HGVSFormat();
    private final InsertionHGVSFormat insertionHGVSFormat = new InsertionHGVSFormat();
    private final SubstitutionHGVSFormat subtitutionHGVFormat = new SubstitutionHGVSFormat();
    private final DeletionHGVSFormat deletionHGVSFormat = new DeletionHGVSFormat();
    private final DeletionInsertionHGVSFormat deletionInsertionHGVSFormat = new DeletionInsertionHGVSFormat();
    private final FrameshiftHGVSFormat frameshiftHGVSFormat = new FrameshiftHGVSFormat();

    @Override
    protected ChangingAAsFormat getChangingAAsFormat() {
        return changingAAsFormat;
    }

    @Override
    protected ProteinSequenceChangeFormat<Substitution> getSubstitutionFormat() {
        return subtitutionHGVFormat;
    }

    @Override
    protected ProteinSequenceChangeFormat<Insertion> getInsertionFormat() {
        return insertionHGVSFormat;
    }

    @Override
    protected ProteinSequenceChangeFormat<Deletion> getDeletionFormat() {
        return deletionHGVSFormat;
    }

    @Override
    protected ProteinSequenceChangeFormat<DeletionAndInsertion> getDeletionInsertionFormat() {
        return deletionInsertionHGVSFormat;
    }

    @Override
    protected ProteinSequenceChangeFormat<Frameshift> getFrameshiftFormat() {
        return frameshiftHGVSFormat;
    }
}
