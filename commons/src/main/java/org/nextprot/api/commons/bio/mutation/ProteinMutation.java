package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.Objects;

/**
 * This object can describe protein mutations of type substitution, deletion, delins and frameshift.
 * Its instanciation is based on a fluent interface.
 *
 * Created by fnikitin on 09/07/15.
 */
public class ProteinMutation {

    private final AminoAcidCode firstAffectedAminoAcidCode;
    private final int firstAffectedAminoAcidPos;
    private final AminoAcidCode lastAffectedAminoAcidCode;
    private final int lastAffectedAminoAcidPos;
    private final Mutation mutation;

    private ProteinMutation(ProteinMutationBuilder builder) {

        this.firstAffectedAminoAcidCode = builder.getDataCollector().getFirstAffectedAminoAcidCode();
        this.firstAffectedAminoAcidPos = builder.getDataCollector().getFirstAffectedAminoAcidPos();
        this.lastAffectedAminoAcidCode = builder.getDataCollector().getLastAffectedAminoAcidCode();
        this.lastAffectedAminoAcidPos = builder.getDataCollector().getLastAffectedAminoAcidPos();
        this.mutation = builder.getDataCollector().getMutation();
    }

    public AminoAcidCode getFirstAffectedAminoAcidCode() {
        return firstAffectedAminoAcidCode;
    }

    public int getFirstAffectedAminoAcidPos() {
        return firstAffectedAminoAcidPos;
    }

    public AminoAcidCode getLastAffectedAminoAcidCode() {
        return lastAffectedAminoAcidCode;
    }

    public int getLastAffectedAminoAcidPos() {
        return lastAffectedAminoAcidPos;
    }

    public boolean isAminoAcidRange() {
        return lastAffectedAminoAcidPos - firstAffectedAminoAcidPos > 0;
    }

    public Mutation getMutation() {
        return mutation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProteinMutation)) return false;
        ProteinMutation that = (ProteinMutation) o;
        return Objects.equals(firstAffectedAminoAcidPos, that.firstAffectedAminoAcidPos) &&
                Objects.equals(lastAffectedAminoAcidPos, that.lastAffectedAminoAcidPos) &&
                Objects.equals(firstAffectedAminoAcidCode, that.firstAffectedAminoAcidCode) &&
                Objects.equals(lastAffectedAminoAcidCode, that.lastAffectedAminoAcidCode) &&
                Objects.equals(mutation, that.mutation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos, lastAffectedAminoAcidCode, lastAffectedAminoAcidPos, mutation);
    }

    public static class FluentBuilder implements ProteinMutationBuilder.StartBuilding {

        private final DataCollector dataCollector;

        public FluentBuilder() {
            dataCollector = new DataCollector();
        }

        @Override
        public ProteinMutationBuilder.SingleAminoAcidMutation aminoAcid(AminoAcidCode firstAffectedAminoAcidCode, int firstAffectedAminoAcidPos) {

            dataCollector.setFirstAffectedAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);
            dataCollector.setLastAffectedAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        @Override
        public ProteinMutationBuilder.AminoAcidMutation aminoAcids(AminoAcidCode firstAffectedAminoAcidCode, int firstAffectedAminoAcidPos, AminoAcidCode lastAffectedAminoAcidCode, int lastAffectedAminoAcidPos) {

            Preconditions.checkArgument(firstAffectedAminoAcidPos < lastAffectedAminoAcidPos);

            dataCollector.setFirstAffectedAminoAcid(firstAffectedAminoAcidCode, firstAffectedAminoAcidPos);
            dataCollector.setLastAffectedAminoAcid(lastAffectedAminoAcidCode, lastAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        class MutationActionImpl implements ProteinMutationBuilder.AminoAcidMutation {

            @Override
            public ProteinMutationBuilder deleted() {
                return new DeletionBuilderProtein(dataCollector);
            }

            @Override
            public ProteinMutationBuilder deletedAndInserts(AminoAcidCode... aas) {
                return new DeletionInsertionBuilderProtein(dataCollector, aas);
            }
        }

        class AAMutationActionImpl extends MutationActionImpl implements ProteinMutationBuilder.SingleAminoAcidMutation {

            @Override
            public ProteinMutationBuilder substitutedBy(AminoAcidCode aa) {
                return new SubstitutionBuilderProtein(dataCollector, aa);
            }

            @Override
            public ProteinMutationBuilder thenFrameshift(int stop) {
                return new FrameshiftBuilderProtein(dataCollector, stop);
            }
        }

        abstract class ProteinMutationBuilderImpl implements ProteinMutationBuilder {

            private final DataCollector dataCollector;

            ProteinMutationBuilderImpl(DataCollector dataCollector) {
                this.dataCollector = dataCollector;
            }

            @Override
            public DataCollector getDataCollector() {
                return dataCollector;
            }
        }

        class DeletionBuilderProtein extends ProteinMutationBuilderImpl {

            DeletionBuilderProtein(DataCollector dataCollector) {
                super(dataCollector);

                dataCollector.setMutation(Deletion.getInstance());
            }

            @Override
            public ProteinMutation build() {

                return new ProteinMutation(this);
            }
        }

        class DeletionInsertionBuilderProtein extends ProteinMutationBuilderImpl {

            DeletionInsertionBuilderProtein(DataCollector dataCollector, AminoAcidCode... aas) {
                super(dataCollector);

                dataCollector.setMutation(new DeletionAndInsertion(aas));
            }

            @Override
            public ProteinMutation build() {

                return new ProteinMutation(this);
            }
        }

        class SubstitutionBuilderProtein extends ProteinMutationBuilderImpl {

            SubstitutionBuilderProtein(DataCollector dataCollector, AminoAcidCode aa) {
                super(dataCollector);

                dataCollector.setMutation(new Substitution(aa));
            }

            @Override
            public ProteinMutation build() {

                return new ProteinMutation(this);
            }
        }

        class FrameshiftBuilderProtein extends ProteinMutationBuilderImpl {

            FrameshiftBuilderProtein(DataCollector dataCollector, int stopCodonPos) {
                super(dataCollector);

                dataCollector.setMutation(new Frameshift(stopCodonPos));
            }

            @Override
            public ProteinMutation build() {

                return new ProteinMutation(this);
            }
        }
    }
}
