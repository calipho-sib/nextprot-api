package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.*;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.GeneFeaturePair;
import com.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryAminoAcidException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryPositionException;
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

    private final FeatureQuery query;

    public SequenceFeatureValidator(FeatureQuery query) {
        this.query = query;
    }

    /**
     * Coordinate validation process in the follwing steps:
     *
     * 1. Check that gene name is compatible with protein name
     * 2. Check that first and last amino-acid(s) described by the feature exists on isoform sequence at given positions
     * 3. Check that feature rules are valid on the isoform sequence
     */
    public FeatureQueryResult validate() throws FeatureQueryException {

        try {
            GeneVariationPair geneVariationPair = newGeneVariationPair(query.getFeature());

            checkValidGeneName(geneVariationPair);
            checkFeatureAminoAcidOnIsoform(geneVariationPair.getVariation());
            checkFeatureRules(geneVariationPair.getVariation());

            FeatureQuerySuccess success = new FeatureQuerySuccess(query);
            success.setSequenceVariation(geneVariationPair.getVariation());

            return success;
        } catch (ParseException e) {

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() +
                    GeneFeaturePair.getGeneName(query.getFeature()).length() + 1);

            throw new InvalidFeatureQueryFormatException(query, pe);
        }
    }

    protected abstract GeneVariationPair newGeneVariationPair(String feature) throws ParseException;

    /**
     * Check that gene name is compatible with protein name
     * Part of the contract a validator should implement to validate a feature on an isoform sequence
     */
    protected void checkValidGeneName(GeneVariationPair geneVariationPair) throws IncompatibleGeneAndProteinNameException {

        Entry entry = query.getEntryIsoform().getEntry();

        if (!geneVariationPair.isValidGeneName(entry)) {

            throw new IncompatibleGeneAndProteinNameException(query, geneVariationPair.getGeneName(),
                    entry.getOverview().getGeneNames().stream().map(EntityName::getName).collect(Collectors.toList()));
        }
    }

    /**
     * Check that first and last amino-acid(s) described by the feature exists on isoform sequence at given positions
     * Part of the contract a validator should implement to validate a feature on an isoform sequence.
     *
     * @param variation the variation on which expected changing amino-acids is found
     */
    protected void checkFeatureAminoAcidOnIsoform(SequenceVariation variation) throws FeatureQueryException {

        EntryIsoform entryIsoform = query.getEntryIsoform();

        checkIsoformPos(entryIsoform.getIsoform(), variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()), query);

        checkIsoformPos(entryIsoform.getIsoform(), variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()), query);
    }

    /**
     * Part of the contract a validator should implement to validate a feature on an isoform sequence
     */
    protected void checkFeatureRules(SequenceVariation variation) throws FeatureQueryException {


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
