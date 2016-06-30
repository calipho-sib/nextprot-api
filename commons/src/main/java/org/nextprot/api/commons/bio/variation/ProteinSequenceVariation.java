package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.Objects;

/**
 * This object describes protein sequence variations of many types (substitution, deletion, delins, frameshift, ...)
 * Its instanciation is based on a fluent interface.
 *
 * See specifications at http://varnomen.hgvs.org/recommendations/protein/
 *
 * Created by fnikitin on 09/07/15.
 */
public class ProteinSequenceVariation {

    private final AminoAcidCode firstChangingAminoAcid;
    private final int firstChangingAminoAcidPos;
    private final AminoAcidCode lastChangingAminoAcid;
    private final int lastChangingAminoAcidPos;
    private final ProteinSequenceChange proteinSequenceChange;

    private ProteinSequenceVariation(ProteinSequenceVariationBuilder builder) {

        this.firstChangingAminoAcid = builder.getDataCollector().getFirstChangingAminoAcid();
        this.firstChangingAminoAcidPos = builder.getDataCollector().getFirstChangingAminoAcidPos();
        this.lastChangingAminoAcid = builder.getDataCollector().getLastChangingAminoAcid();
        this.lastChangingAminoAcidPos = builder.getDataCollector().getLastChangingAminoAcidPos();
        this.proteinSequenceChange = builder.getDataCollector().getProteinSequenceChange();
    }

    public AminoAcidCode getFirstChangingAminoAcid() {
        return firstChangingAminoAcid;
    }

    public int getFirstChangingAminoAcidPos() {
        return firstChangingAminoAcidPos;
    }

    public AminoAcidCode getLastChangingAminoAcid() {
        return lastChangingAminoAcid;
    }

    public int getLastChangingAminoAcidPos() {
        return lastChangingAminoAcidPos;
    }

    public boolean isAminoAcidRange() {
        return lastChangingAminoAcidPos - firstChangingAminoAcidPos > 0;
    }

    public ProteinSequenceChange getProteinSequenceChange() {
        return proteinSequenceChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProteinSequenceVariation)) return false;
        ProteinSequenceVariation that = (ProteinSequenceVariation) o;
        return Objects.equals(firstChangingAminoAcidPos, that.firstChangingAminoAcidPos) &&
                Objects.equals(lastChangingAminoAcidPos, that.lastChangingAminoAcidPos) &&
                Objects.equals(firstChangingAminoAcid, that.firstChangingAminoAcid) &&
                Objects.equals(lastChangingAminoAcid, that.lastChangingAminoAcid) &&
                Objects.equals(proteinSequenceChange, that.proteinSequenceChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstChangingAminoAcid, firstChangingAminoAcidPos, lastChangingAminoAcid, lastChangingAminoAcidPos, proteinSequenceChange);
    }

    @Override
    public String toString() {
        return firstChangingAminoAcid + "[" + firstChangingAminoAcidPos + "] ... " +
                lastChangingAminoAcid + "[" + lastChangingAminoAcidPos + "]) -> " +
                proteinSequenceChange.getValue();
    }

    public static class FluentBuilder implements ProteinSequenceVariationBuilder.StartBuilding {

        private final ProteinSequenceVariationBuilder.DataCollector dataCollector;

        public FluentBuilder() {
            dataCollector = new ProteinSequenceVariationBuilder.DataCollector();
        }

        @Override
        public ProteinSequenceVariationBuilder.SingleAminoAcidMutation aminoAcid(AminoAcidCode firstAffectedAminoAcid, int firstAffectedAminoAcidPos) {

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        @Override
        public ProteinSequenceVariationBuilder.AminoAcidMutation aminoAcids(AminoAcidCode firstAffectedAminoAcid, int firstAffectedAminoAcidPos, AminoAcidCode lastAffectedAminoAcid, int lastAffectedAminoAcidPos) {

            Preconditions.checkArgument(firstAffectedAminoAcidPos < lastAffectedAminoAcidPos);

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(lastAffectedAminoAcid, lastAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        class MutationActionImpl implements ProteinSequenceVariationBuilder.AminoAcidMutation {

            @Override
            public ProteinSequenceVariationBuilder deletes() {
                return new DeletionBuilderProtein(dataCollector);
            }

            @Override
            public ProteinSequenceVariationBuilder inserts(AminoAcidCode... aas) {
                return new InsertionBuilderProtein(dataCollector, aas);
            }

            @Override
            public ProteinSequenceVariationBuilder duplicates() {
                return new DuplicationBuilderProtein(dataCollector);
            }

            @Override
            public ProteinSequenceVariationBuilder deletedAndInserts(AminoAcidCode... aas) {
                return new DeletionInsertionBuilderProtein(dataCollector, aas);
            }
        }

        class AAMutationActionImpl extends MutationActionImpl implements ProteinSequenceVariationBuilder.SingleAminoAcidMutation {

            @Override
            public ProteinSequenceVariationBuilder substitutedBy(AminoAcidCode aa) {
                return new SubstitutionBuilderProtein(dataCollector, aa);
            }

            @Override
            public ProteinSequenceVariationBuilder thenFrameshift(AminoAcidCode newAminoAcidCode, int newTerminationPosition) {
                return new FrameshiftBuilderProtein(dataCollector, newAminoAcidCode, newTerminationPosition);
            }
        }

        abstract class ProteinSequenceVariationBuilderImpl implements ProteinSequenceVariationBuilder {

            private final DataCollector dataCollector;

            ProteinSequenceVariationBuilderImpl(DataCollector dataCollector) {
                this.dataCollector = dataCollector;
            }

            protected abstract ProteinSequenceChange getProteinSequenceChange();

            @Override
            public DataCollector getDataCollector() {
                return dataCollector;
            }

            @Override
            public ProteinSequenceVariation build() {

                dataCollector.setProteinSequenceChange(getProteinSequenceChange());
                return new ProteinSequenceVariation(this);
            }
        }

        class DeletionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            DeletionBuilderProtein(DataCollector dataCollector) {
                super(dataCollector);
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return Deletion.getInstance();
            }
        }

        class InsertionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            private final AminoAcidCode[] insertedAas;

            InsertionBuilderProtein(DataCollector dataCollector, AminoAcidCode... insertedAas) {
                super(dataCollector);

                this.insertedAas = insertedAas;
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return new Insertion(dataCollector.getFirstChangingAminoAcidPos(), insertedAas);
            }

            AminoAcidCode[] getInsertedAas() {
                return insertedAas;
            }
        }

        class DeletionInsertionBuilderProtein extends InsertionBuilderProtein {

            DeletionInsertionBuilderProtein(DataCollector dataCollector, AminoAcidCode... aas) {
                super(dataCollector, aas);
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return new DeletionAndInsertion(getInsertedAas());
            }
        }

        class DuplicationBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            DuplicationBuilderProtein(DataCollector dataCollector) {
                super(dataCollector);
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return new Duplication(dataCollector.getLastChangingAminoAcidPos());
            }
        }

        class SubstitutionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            private final AminoAcidCode substitutedAminoAcid;

            SubstitutionBuilderProtein(DataCollector dataCollector, AminoAcidCode substitutedAminoAcid) {
                super(dataCollector);
                this.substitutedAminoAcid = substitutedAminoAcid;
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return new Substitution(substitutedAminoAcid);
            }
        }

        class FrameshiftBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            private final AminoAcidCode newAminoAcidCode;
            private final int newTerminationPosition;

            FrameshiftBuilderProtein(DataCollector dataCollector, AminoAcidCode newAminoAcidCode, int newTerminationPosition) {
                super(dataCollector);

                this.newAminoAcidCode = newAminoAcidCode;
                this.newTerminationPosition = newTerminationPosition;
            }

            @Override
            protected ProteinSequenceChange getProteinSequenceChange() {
                return new Frameshift(new Frameshift.Change(newAminoAcidCode, newTerminationPosition));
            }
        }
    }
}
