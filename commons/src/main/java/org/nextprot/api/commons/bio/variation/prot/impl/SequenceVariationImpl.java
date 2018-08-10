package org.nextprot.api.commons.bio.variation.prot.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.*;
import org.nextprot.api.commons.bio.variation.prot.impl.varseq.VaryingSequenceImpl;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

import java.util.Objects;

/**
 * This object describes protein sequence variations of many types (substitution, deletion, delins, frameshift, ...)
 * Its instanciation is based on a fluent interface.
 *
 * See specifications at http://varnomen.hgvs.org/recommendations/protein/
 *
 * Created by fnikitin on 09/07/15.
 */
public class SequenceVariationImpl implements SequenceVariation {

    private final VaryingSequence varyingSequence;
    private final SequenceChange<?> sequenceChange;

    private SequenceVariationImpl(SequenceVariationBuilder builder) {

        varyingSequence = new VaryingSequenceImpl(builder.getDataCollector().getFirstChangingAminoAcid(),
            builder.getDataCollector().getFirstChangingAminoAcidPos(),
            builder.getDataCollector().getLastChangingAminoAcid(),
            builder.getDataCollector().getLastChangingAminoAcidPos());

        sequenceChange = builder.getDataCollector().getSequenceChange();
    }

    @Override
    public VaryingSequence getVaryingSequence() {
        return varyingSequence;
    }

    public SequenceChange<?> getSequenceChange() {
        return sequenceChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SequenceVariationImpl)) return false;
        SequenceVariationImpl that = (SequenceVariationImpl) o;
        return Objects.equals(varyingSequence, that.varyingSequence) &&
                Objects.equals(sequenceChange, that.sequenceChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varyingSequence, sequenceChange);
    }

    @Override
    public String toString() {
        return "SequenceVariationImpl{" +
                "changingSequence=" + varyingSequence +
                ", sequenceChange=" + sequenceChange +
                '}';
    }

    public static class StartBuilding implements SequenceVariationBuilder.StartBuilding {

        private final SequenceVariationBuilder.DataCollector dataCollector;

        public StartBuilding() {
            dataCollector = new SequenceVariationBuilder.DataCollector();
        }

        @Override
        public SequenceVariationBuilder.StartBuildingFromAAs fromAAs(String aas) {

            return new StartBuildingFromAAsImpl(aas);
        }

        @Override
        public SequenceVariationBuilder.ChangingAminoAcid selectAminoAcid(AminoAcidCode firstAffectedAminoAcid, int firstAffectedAminoAcidPos) {

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        @Override
        public SequenceVariationBuilder.ChangingAminoAcidRange selectAminoAcidRange(AminoAcidCode firstAffectedAminoAcid, int firstAffectedAminoAcidPos, AminoAcidCode lastAffectedAminoAcid, int lastAffectedAminoAcidPos) {

            Preconditions.checkArgument(firstAffectedAminoAcidPos < lastAffectedAminoAcidPos);

            dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAminoAcidPos);
            dataCollector.setLastChangingAminoAcid(lastAffectedAminoAcid, lastAffectedAminoAcidPos);

            return new AAMutationActionImpl();
        }

        class StartBuildingFromAAsImpl implements SequenceVariationBuilder.StartBuildingFromAAs {

            private final String aas;

            StartBuildingFromAAsImpl(String aas) {

                this.aas = aas;
            }

            @Override
            public SequenceVariationBuilder.ChangingAminoAcid selectAminoAcid(int affectedAAPos) {

                Preconditions.checkArgument(affectedAAPos <= aas.length());

                AminoAcidCode firstAffectedAminoAcid = AminoAcidCode.valueOfAminoAcid(String.valueOf(aas.charAt(affectedAAPos-1)));

                dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, affectedAAPos);
                dataCollector.setLastChangingAminoAcid(firstAffectedAminoAcid, affectedAAPos);

                return new AAMutationActionImpl();
            }

            @Override
            public SequenceVariationBuilder.ChangingAminoAcidRange selectAminoAcidRange(int firstAffectedAAPos, int lastAffectedAAPos) {

                Preconditions.checkArgument(firstAffectedAAPos <= aas.length());
                Preconditions.checkArgument(lastAffectedAAPos <= aas.length());
                Preconditions.checkArgument(firstAffectedAAPos < lastAffectedAAPos);

                AminoAcidCode firstAffectedAminoAcid = AminoAcidCode.valueOfAminoAcid(String.valueOf(aas.charAt(firstAffectedAAPos-1)));
                AminoAcidCode lastAffectedAminoAcid = AminoAcidCode.valueOfAminoAcid(String.valueOf(aas.charAt(lastAffectedAAPos-1)));


                dataCollector.setFirstChangingAminoAcid(firstAffectedAminoAcid, firstAffectedAAPos);
                dataCollector.setLastChangingAminoAcid(lastAffectedAminoAcid, lastAffectedAAPos);

                return new AAMutationActionImpl();
            }
        }

        class MutationActionImpl implements SequenceVariationBuilder.ChangingAminoAcidRange {

            @Override
            public SequenceVariationBuilder thenDelete() {
                return new DeletionBuilder(dataCollector);
            }

            @Override
            public SequenceVariationBuilder thenInsert(AminoAcidCode... aas) {
                return new InsertionBuilder(dataCollector, aas);
            }

            @Override
            public SequenceVariationBuilder thenDuplicate() {
                return new DuplicationBuilder(dataCollector);
            }

            @Override
            public SequenceVariationBuilder thenDeleteAndInsert(AminoAcidCode... aas) {
                return new DeletionInsertionBuilder(dataCollector, aas);
            }
        }

        class AAMutationActionImpl extends MutationActionImpl implements SequenceVariationBuilder.ChangingAminoAcid {

            @Override
            public SequenceVariationBuilder thenSubstituteWith(AminoAcidCode aa) {
                return new SubstitutionBuilder(dataCollector, aa);
            }

            @Override
            public SequenceVariationBuilder thenFrameshift(AminoAcidCode newAminoAcidCode, int newTerminationPosition) {
                return new FrameshiftBuilder(dataCollector, newAminoAcidCode, newTerminationPosition);
            }

            @Override
            public SequenceVariationBuilder thenAddModification(SequenceChange<AminoAcidModification> mod) {
                return new AminoAcidModificationBuilder(dataCollector, mod);
            }

            @Override
            public SequenceVariationBuilder thenInitiationExtension(int newUpstreamInitPos, AminoAcidCode newAminoAcidCode) {
                return new InitiationExtensionBuilder(dataCollector, newUpstreamInitPos, newAminoAcidCode);
            }

            @Override
            public SequenceVariationBuilder thenTerminationExtension(int newDownstreamTermPos, AminoAcidCode newAminoAcidCode) {

                return new TerminationExtensionBuilder(dataCollector, newDownstreamTermPos, newAminoAcidCode);
            }
        }

        abstract class SequenceVariationBuilderImpl implements SequenceVariationBuilder {

            private final DataCollector dataCollector;

            SequenceVariationBuilderImpl(DataCollector dataCollector) {
                this.dataCollector = dataCollector;
            }

            protected abstract SequenceChange getProteinSequenceChange();

            @Override
            public DataCollector getDataCollector() {
                return dataCollector;
            }

            @Override
            public SequenceVariation build() {

                dataCollector.setSequenceChange(getProteinSequenceChange());
                return new SequenceVariationImpl(this);
            }
        }

        class DeletionBuilder extends SequenceVariationBuilderImpl {

            DeletionBuilder(DataCollector dataCollector) {
                super(dataCollector);
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {
                return new Deletion();
            }
        }

        class InsertionBuilder extends SequenceVariationBuilderImpl {

            private final AminoAcidCode[] insertedAas;

            InsertionBuilder(DataCollector dataCollector, AminoAcidCode... insertedAas) {
                super(dataCollector);

                this.insertedAas = insertedAas;
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {
                return new Insertion(dataCollector.getFirstChangingAminoAcidPos(), insertedAas);
            }

            AminoAcidCode[] getInsertedAas() {
                return insertedAas;
            }
        }

        class DeletionInsertionBuilder extends InsertionBuilder {

            DeletionInsertionBuilder(DataCollector dataCollector, AminoAcidCode... aas) {
                super(dataCollector, aas);
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {
                return new DeletionAndInsertion(getInsertedAas());
            }
        }

        class DuplicationBuilder extends SequenceVariationBuilderImpl {

            DuplicationBuilder(DataCollector dataCollector) {
                super(dataCollector);
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {

                return new Duplication(dataCollector.getLastChangingAminoAcidPos());
            }
        }

        class SubstitutionBuilder extends SequenceVariationBuilderImpl {

            private final AminoAcidCode substitutedAminoAcid;

            SubstitutionBuilder(DataCollector dataCollector, AminoAcidCode substitutedAminoAcid) {
                super(dataCollector);
                this.substitutedAminoAcid = substitutedAminoAcid;
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {
                return new Substitution(substitutedAminoAcid);
            }
        }

        class FrameshiftBuilder extends SequenceVariationBuilderImpl {

            private final AminoAcidCode newAminoAcidCode;
            private final int newTerminationPosition;

            FrameshiftBuilder(DataCollector dataCollector, AminoAcidCode newAminoAcidCode, int newTerminationPosition) {
                super(dataCollector);

                this.newAminoAcidCode = newAminoAcidCode;
                this.newTerminationPosition = newTerminationPosition;
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {

                return new Frameshift(new Frameshift.Change(newAminoAcidCode, newTerminationPosition));
            }
        }

        class AminoAcidModificationBuilder extends SequenceVariationBuilderImpl {

            private final SequenceChange<AminoAcidModification> mod;

            AminoAcidModificationBuilder(DataCollector dataCollector, SequenceChange<AminoAcidModification> mod) {
                super(dataCollector);
                this.mod = mod;
            }

            @Override
            protected SequenceChange<AminoAcidModification> getProteinSequenceChange() {
                return mod;
            }
        }

        class InitiationExtensionBuilder extends SequenceVariationBuilderImpl {

            private final ExtensionInitiation extension;

            InitiationExtensionBuilder(DataCollector dataCollector, int newUpstreamSitePos, AminoAcidCode newAminoAcidCode) {

                super(dataCollector);

                this.extension = new ExtensionInitiation(newUpstreamSitePos, newAminoAcidCode);
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {

                return extension;
            }
        }

        class TerminationExtensionBuilder extends SequenceVariationBuilderImpl {

            private final ExtensionTermination extension;

            TerminationExtensionBuilder(DataCollector dataCollector, int newDownstreamTermPos, AminoAcidCode newAminoAcidCode) {

                super(dataCollector);

                if (dataCollector.getFirstChangingAminoAcid() != AminoAcidCode.STOP) {
                    throw new IllegalStateException("Invalid termination extension: first amino-acid should be a STOP but is a " + dataCollector.getFirstChangingAminoAcid());
                }

                this.extension = new ExtensionTermination(newDownstreamTermPos, newAminoAcidCode);
            }

            @Override
            protected SequenceChange getProteinSequenceChange() {

                return extension;
            }
        }
    }

}
