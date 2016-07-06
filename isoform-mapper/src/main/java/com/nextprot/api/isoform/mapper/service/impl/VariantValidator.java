package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.*;
import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantSplitter;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validate variant type features
 *
 * Created by fnikitin on 05/07/16.
 */
class VariantValidator implements FeatureValidator {

    @Override
    public FeatureQueryResult validate(FeatureQuery query, EntryIsoform entryIsoform) throws FeatureQueryException {

        try {
            GeneVariantSplitter splitter = new GeneVariantSplitter(query.getFeature());
            if (!splitter.isValidGeneName(entryIsoform.getEntry())) {

                List<String> expectedGeneNames = entryIsoform.getEntry().getOverview().getGeneNames().stream()
                        .map(EntityName::getName).collect(Collectors.toList());

                throw new IncompatibleGeneAndProteinNameException(query, splitter.getGeneName(), expectedGeneNames);
            }

            ProteinSequenceVariation entryIsoformVariation = splitter.getVariant();

            return checkFeatureOnIsoform(query, entryIsoform.getIsoform(), entryIsoformVariation);
        } catch (ParseException e) {

            String geneName = GeneVariantSplitter.getGeneName(query.getFeature());

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() + geneName.length() + 1);
            throw new InvalidFeatureQueryFormatException(query, pe);
        }
    }

    /**
     * Check that variating amino-acid(s) on isoform sequence exists and return result
     *
     * @param isoform   the isoform to check variating amino-acids
     * @param variation the variation on which expected changing amino-acids is found
     */
    private FeatureQueryResult checkFeatureOnIsoform(FeatureQuery query, Isoform isoform,
                                                     ProteinSequenceVariation variation) throws FeatureQueryException {

        checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()), query);

        checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()), query);

        FeatureQueryResult result = new FeatureQuerySuccess(query);

        ((FeatureQuerySuccess) result).addMappedFeature(isoform.getUniqueName(),
                variation.getFirstChangingAminoAcidPos(), variation.getLastChangingAminoAcidPos());

        return result;
    }

    /**
     * @param isoform
     * @param position
     * @param aas
     * @throws FeatureQueryException if invalid
     */
    private void checkIsoformPos(Isoform isoform, int position, String aas, FeatureQuery query) throws FeatureQueryException {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            throw new InvalidFeatureQueryPositionException(query, position);
        }

        if (!insertionMode) {
            valid = IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas);

            if (!valid) {

                String aasOnSequence = isoform.getSequence().substring(position - 1, position + aas.length() - 1);

                throw new InvalidFeatureQueryAminoAcidException(query, position,
                        AminoAcidCode.valueOfOneLetterCodeSequence(aasOnSequence),
                        AminoAcidCode.valueOfOneLetterCodeSequence(aas));
            }
        }
    }
}
