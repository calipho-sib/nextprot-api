package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.exception.*;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
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
    private final FeatureQuery query;

    public SequenceFeatureValidator(Entry entry, FeatureQuery query) {
        this.entry = entry;
        this.query = query;
    }

    /**
     * Coordinate validation process in the multiple steps defined in protected methods.
     */
    public FeatureQueryResult validate(SequenceFeature sequenceFeature) throws FeatureQueryException {

        checkFeatureGeneName(sequenceFeature);
        checkIsoformExistence(sequenceFeature);
        checkFeatureChangingAminoAcids(sequenceFeature);

        doMoreChecks(sequenceFeature.getProteinVariation());

        return new FeatureQuerySuccess(entry, query, sequenceFeature);
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

        checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                variation.getFirstChangingAminoAcid().get1LetterCode(), query);

        checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
                variation.getLastChangingAminoAcid().get1LetterCode(), query);
    }

    /**
     * Check that the given amino-acid(s) exist(s) at the given position of given isoform sequence
     * @throws FeatureQueryException if invalid
     */
    private void checkIsoformPos(Isoform isoform, int position, String aas, FeatureQuery query) throws FeatureQueryException {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            throw new OutOfBoundSequencePositionException(query, position);
        }

        if (!insertionMode && !IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas)) {

            String aasOnSequence = isoform.getSequence().substring(position - 1, position + aas.length() - 1);

            throw new UnexpectedFeatureQueryAminoAcidException(query, position,
                    AminoAcidCode.valueOfOneLetterCodeSequence(aasOnSequence),
                    AminoAcidCode.valueOfOneLetterCodeSequence(aas));
        }
    }
}
