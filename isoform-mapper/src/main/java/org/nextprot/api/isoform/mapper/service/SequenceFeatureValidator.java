package org.nextprot.api.isoform.mapper.service;

import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.impl.BaseFeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.exception.*;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.util.stream.Collectors;

/**
 * Validate variant type features on isoform sequence
 *
 * Created by fnikitin on 05/07/16.
 */
public class SequenceFeatureValidator {

    private final Entry entry;
    private final SingleFeatureQuery query;

    public SequenceFeatureValidator(Entry entry, SingleFeatureQuery query) {
        this.entry = entry;
        this.query = query;
    }

    /**
     * Coordinate validation process in the multiple steps defined in protected methods.
     */
    public BaseFeatureQueryResult validate(SequenceFeature sequenceFeature) throws FeatureQueryException {

        checkFeatureGeneName(sequenceFeature);
        checkIsoformExistence(sequenceFeature);
        checkFeatureChangingAminoAcids(sequenceFeature);

        doMoreChecks(sequenceFeature.getProteinVariation());

        return new SingleFeatureQuerySuccessImpl(entry, query, sequenceFeature);
    }

    private void checkIsoformExistence(SequenceFeature sequenceFeature) throws UnknownFeatureIsoformException {

        if (!sequenceFeature.isValidIsoform(entry)) {

            throw new UnknownFeatureIsoformException(entry, query, sequenceFeature.getIsoformName());
        }
    }

    /**
     * Do more feature validation (nothing by default). It is supposed to be overiden by validators that need to
     * do more validations.
     *
     * @param sequenceVariation the sequence variation
     * @throws FeatureQueryException if invalid
     */
    protected void doMoreChecks(SequenceVariation sequenceVariation) throws FeatureQueryException { }

    /**
     * Check that gene name is compatible with protein name
     * Part of the contract a validator should implement to validate a feature on an isoform sequence
     */
    private void checkFeatureGeneName(SequenceFeature sequenceFeature) throws IncompatibleGeneAndProteinNameException {

        if (!sequenceFeature.isValidGeneName(entry)) {

            throw new IncompatibleGeneAndProteinNameException(query, sequenceFeature.getGeneName(),
                    entry.getOverview().getGeneNames().stream().map(EntityName::getName).collect(Collectors.toList()));

        }
    }

    /**
     * Check that first and last amino-acid(s) described by the feature exists on isoform sequence at given positions
     * Part of the contract a validator should implement to validate a feature on an isoform sequence.
     */
    private void checkFeatureChangingAminoAcids(SequenceFeature sequenceFeature) throws FeatureQueryException {

        SequenceVariation variation = sequenceFeature.getProteinVariation();

        Isoform isoform = sequenceFeature.getIsoform(entry);

        // do check only position for STOP code
        if (sequenceFeature.getProteinVariation().getFirstChangingAminoAcid() == AminoAcidCode.STOP) {
            checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos()-1, query, false);
        }
        else {
            checkIsoformPosAndAminoAcids(isoform, variation.getFirstChangingAminoAcidPos(), variation.getFirstChangingAminoAcid().get1LetterCode(), query);
        }

        if (sequenceFeature.getProteinVariation().getLastChangingAminoAcid() == AminoAcidCode.STOP) {
            checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos()-1, query, false);
        }
        else {
            checkIsoformPosAndAminoAcids(isoform, variation.getLastChangingAminoAcidPos(), variation.getLastChangingAminoAcid().get1LetterCode(), query);
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
