package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.mutation.*;

/**
 * <code>ProteinMutationHGVFormat</code> can format and parse
 * ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @link http://www.hgvs.org/mutnomen/recs-prot.html#prot
 *
 * Created by fnikitin on 10/07/15.
 */
public class ProteinMutationHGVFormat extends AbstractProteinMutationFormat {

    private final MutatedAAsFormat mutatedAAsFormat = new MutatedAAsHGVFormat();
    private final InsertionHGVFormat insertionHGVFormat = new InsertionHGVFormat();
    private final SubstitutionHGVFormat subtitutionHGVFormat = new SubstitutionHGVFormat();
    private final DeletionHGVFormat deletionHGVFormat = new DeletionHGVFormat();
    private final DeletionInsertionHGVFormat deletionInsertionHGVFormat = new DeletionInsertionHGVFormat();
    private final FrameshiftHGVFormat frameshiftHGVFormat = new FrameshiftHGVFormat();

    @Override
    protected MutatedAAsFormat getAffectedAAsFormat() {
        return mutatedAAsFormat;
    }

    @Override
    protected MutationEffectFormat<Substitution> getSubstitutionFormat() {
        return subtitutionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Insertion> getInsertionFormat() {
        return insertionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Deletion> getDeletionFormat() {
        return deletionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<DeletionAndInsertion> getDeletionInsertionFormat() {
        return deletionInsertionHGVFormat;
    }

    @Override
    protected MutationEffectFormat<Frameshift> getFrameshiftFormat() {
        return frameshiftHGVFormat;
    }
}
