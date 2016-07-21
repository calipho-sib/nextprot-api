package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.SequenceFeatureBase;
import com.nextprot.api.isoform.mapper.domain.impl.exception.*;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.stream.Collectors;

/**
 * Validate variant type features on isoform sequence
 *
 * Created by fnikitin on 05/07/16.
 */
public abstract class SequenceFeatureValidator {

    protected final FeatureQuery query;

    public SequenceFeatureValidator(FeatureQuery query) {
        this.query = query;
    }

    /**
     * Coordinate validation process in the multiple steps defined in protected methods.
     */
    public FeatureQueryResult validate() throws FeatureQueryException {

        try {
            SequenceFeature sequenceFeature = newSequenceFeature(query.getFeature());

            checkFeatureGeneName(sequenceFeature);
            checkIsoformExistence(sequenceFeature);
            checkFeatureChangingAminoAcids(sequenceFeature);

            doMoreChecks(sequenceFeature.getProteinVariation());

            return new FeatureQuerySuccess(query, sequenceFeature);
        } catch (ParseException e) {

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() +
                    SequenceFeatureBase.getGeneName(query.getFeature()).length() + 1);

            throw new InvalidFeatureQueryFormatException(query, pe);
        }
    }

    private void checkIsoformExistence(SequenceFeature sequenceFeature) throws UnknownFeatureIsoformException {

        if (!sequenceFeature.isValidIsoform(query.getEntry())) {

            throw new UnknownFeatureIsoformException(query);
        }
    }

    protected abstract SequenceFeature newSequenceFeature(String feature) throws ParseException;

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

        Entry entry = query.getEntry();

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

        Isoform isoform = sequenceFeature.getIsoform(query.getEntry());

        checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()), query);

        checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()), query);
    }

    /**
     * Check that the given amino-acid(s) exist(s) at the given position of given isoform sequence
     * @throws FeatureQueryException if invalid
     */
    private void checkIsoformPos(Isoform isoform, int position, String aas, FeatureQuery query) throws FeatureQueryException {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            throw new InvalidFeatureQueryPositionException(query, position);
        }

        if (!insertionMode && !IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas)) {

            String aasOnSequence = isoform.getSequence().substring(position - 1, position + aas.length() - 1);

            throw new InvalidFeatureQueryAminoAcidException(query, position,
                    AminoAcidCode.valueOfOneLetterCodeSequence(aasOnSequence),
                    AminoAcidCode.valueOfOneLetterCodeSequence(aas));
        }
    }
}
