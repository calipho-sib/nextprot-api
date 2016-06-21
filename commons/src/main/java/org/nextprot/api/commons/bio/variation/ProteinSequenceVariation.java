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

    public static class FluentBuilder implements ProteinSequenceVariationBuilder.StartBuilding {

        private final ProteinSequenceVariationBuilder.DataCollector dataCollector;

        public FluentBuilder() {
            dataCollector = new ProteinSequenceVariationBuilder.DataCollector();
        }

        @Override
        public ProteinSequenceVariationBuilder.SingleAminoAcidMutation aminoAcid(AminoAcidCode firstAffectedAminoAcidCode, int firstAffectedAminoAcidPos) {

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        @Override
        public ProteinSequenceVariationBuilder.AminoAcidMutation aminoAcids(AminoAcidCode firstAffectedAminoAcidCode, int firstAffectedAminoAcidPos, AminoAcidCode lastAffectedAminoAcidCode, int lastAffectedAminoAcidPos) {

            Preconditions.checkArgument(firstAffectedAminoAcidPos < lastAffectedAminoAcidPos);

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(lastAffectedAminoAcidCode, lastAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        class MutationActionImpl implements ProteinSequenceVariationBuilder.AminoAcidMutation {

            @Override
            public ProteinSequenceVariationBuilder deleted() {
                return new DeletionBuilderProtein(dataCollector);
            }

            @Override
            public ProteinSequenceVariationBuilder inserts(AminoAcidCode... aas) {
                return new InsertionBuilderProtein(dataCollector, aas);
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
            public ProteinSequenceVariationBuilder thenFrameshift(int stop) {
                return new FrameshiftBuilderProtein(dataCollector, stop);
            }
        }

        abstract class ProteinSequenceVariationBuilderImpl implements ProteinSequenceVariationBuilder {

            private final DataCollector dataCollector;

            ProteinSequenceVariationBuilderImpl(DataCollector dataCollector) {
                this.dataCollector = dataCollector;
            }

            @Override
            public DataCollector getDataCollector() {
                return dataCollector;
            }
        }

        class DeletionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            DeletionBuilderProtein(DataCollector dataCollector) {
                super(dataCollector);

                dataCollector.setProteinSequenceChange(Deletion.getInstance());
            }

            @Override
            public ProteinSequenceVariation build() {

                return new ProteinSequenceVariation(this);
            }
        }

        class DeletionInsertionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            DeletionInsertionBuilderProtein(DataCollector dataCollector, AminoAcidCode... aas) {
                super(dataCollector);

                dataCollector.setProteinSequenceChange(new DeletionAndInsertion(aas));
            }

            @Override
            public ProteinSequenceVariation build() {

                return new ProteinSequenceVariation(this);
            }
        }

        class InsertionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            InsertionBuilderProtein(DataCollector dataCollector, AminoAcidCode... aas) {
                super(dataCollector);

                dataCollector.setProteinSequenceChange(new Insertion(dataCollector.getFirstChangingAminoAcidPos(), aas));
            }

            @Override
            public ProteinSequenceVariation build() {

                return new ProteinSequenceVariation(this);
            }
        }

        class SubstitutionBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            SubstitutionBuilderProtein(DataCollector dataCollector, AminoAcidCode aa) {
                super(dataCollector);

                dataCollector.setProteinSequenceChange(new Substitution(aa));
            }

            @Override
            public ProteinSequenceVariation build() {

                return new ProteinSequenceVariation(this);
            }
        }

        class FrameshiftBuilderProtein extends ProteinSequenceVariationBuilderImpl {

            FrameshiftBuilderProtein(DataCollector dataCollector, int stopCodonPos) {
                super(dataCollector);

                dataCollector.setProteinSequenceChange(new Frameshift(stopCodonPos));
            }

            @Override
            public ProteinSequenceVariation build() {

                return new ProteinSequenceVariation(this);
            }
        }
    }
}
