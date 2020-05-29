package org.nextprot.api.isoform.mapper.domain.feature;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.feature.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.BaseFeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.exception.OutOfBoundSequencePositionException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnexpectedFeatureQueryAminoAcidException;

/**
 * Validate sequence feature type features on isoform sequence
 *
 * Created by fnikitin on 05/07/16.
 */
public abstract class SequenceFeatureValidator<SF extends SequenceFeature> {

    protected final SingleFeatureQuery query;

    public SequenceFeatureValidator(SingleFeatureQuery query) {
        this.query = query;
    }

    /**
     * Coordinate validation process in the multiple steps defined in protected methods.
     */
    public BaseFeatureQueryResult validate(SF sequenceFeature) throws FeatureQueryException {

        preChecks(sequenceFeature);
        checkVaryingAminoAcids(sequenceFeature);
        postChecks(sequenceFeature);

        return new SingleFeatureQuerySuccessImpl(query, sequenceFeature);
    }

    /**
     * nothing by default. It is supposed to be overridden by validators that need to do pre-check validations.
     *
     * @param sequenceFeature the sequence feature
     * @throws FeatureQueryException if invalid
     */
    protected void preChecks(SF sequenceFeature) throws FeatureQueryException { }

    /**
     * nothing by default. It is supposed to be overridden by validators that need to do post-check validations.
     *
     * @param sequenceFeature the sequence feature
     * @throws FeatureQueryException if invalid
     */
    protected void postChecks(SF sequenceFeature) throws FeatureQueryException { }

    /**
     * Check that first and last amino-acid(s) described by the feature exists on isoform sequence at given positions
     * Part of the contract a validator should implement to validate a feature on an isoform sequence.
     */
    private void checkVaryingAminoAcids(SF sequenceFeature) throws FeatureQueryException {

        SequenceVariation variation = sequenceFeature.getProteinVariation();

        Isoform isoform = sequenceFeature.getIsoform();

        // do check only position for STOP code
        if (sequenceFeature.getProteinVariation().getVaryingSequence().getFirstAminoAcid() == AminoAcidCode.STOP) {
            checkIsoformPos(isoform, variation.getVaryingSequence().getFirstAminoAcidPos()-1, query, false);
        }
        else {
            checkIsoformPosAndAminoAcids(isoform, variation.getVaryingSequence().getFirstAminoAcidPos(), variation.getVaryingSequence().getFirstAminoAcid().get1LetterCode(), query);
        }

        if (sequenceFeature.getProteinVariation().getVaryingSequence().getLastAminoAcid() == AminoAcidCode.STOP) {
            checkIsoformPos(isoform, variation.getVaryingSequence().getLastAminoAcidPos()-1, query, false);
        }
        else {
            checkIsoformPosAndAminoAcids(isoform, variation.getVaryingSequence().getLastAminoAcidPos(), variation.getVaryingSequence().getLastAminoAcid().get1LetterCode(), query);
        }
    }

    /**
     * Check that the given amino-acid(s) exist(s) at the given position of given isoform sequence
     * @throws FeatureQueryException if invalid
     */
    private void checkIsoformPosAndAminoAcids(Isoform isoform, int position, String aas, SingleFeatureQuery query) throws FeatureQueryException {

        boolean insertionMode = (aas == null || aas.isEmpty());

        checkIsoformPos(isoform, position, query, insertionMode);

        if (!insertionMode && !IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas)) {

            String aasOnSequence = isoform.getSequence().substring(position - 1, position + aas.length() - 1);

            throw new UnexpectedFeatureQueryAminoAcidException(query, position,
                    AminoAcidCode.valueOfAminoAcidCodeSequence(aasOnSequence),
                    AminoAcidCode.valueOfAminoAcidCodeSequence(aas));
        }
    }

    private void checkIsoformPos(Isoform isoform, int position, SingleFeatureQuery query, boolean insertionMode) throws FeatureQueryException {

        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            throw new OutOfBoundSequencePositionException(query, position);
        }
    }
}
