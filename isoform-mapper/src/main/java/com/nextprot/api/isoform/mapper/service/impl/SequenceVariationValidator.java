package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.GeneVariationPair;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.GeneFeaturePair;
import com.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryAminoAcidException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryPositionException;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validate variant type features on isoform sequence
 *
 * Created by fnikitin on 05/07/16.
 */
public abstract class SequenceVariationValidator implements SequenceFeatureValidator {

    @Override
    public FeatureQueryResult validate(FeatureQuery query, EntryIsoform entryIsoform) throws FeatureQueryException {

        try {
            GeneVariationPair geneVariationPair = newGeneVariationPair(query.getFeature());

            if (!geneVariationPair.isValidGeneName(entryIsoform.getEntry())) {

                List<String> expectedGeneNames = entryIsoform.getEntry().getOverview().getGeneNames().stream()
                        .map(EntityName::getName).collect(Collectors.toList());

                throw new IncompatibleGeneAndProteinNameException(query, geneVariationPair.getGeneName(), expectedGeneNames);
            }
            return checkFeatureOnIsoform(query, entryIsoform, geneVariationPair.getVariation());

        } catch (ParseException e) {

            String geneName = GeneFeaturePair.getGeneName(query.getFeature());

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() + geneName.length() + 1);
            throw new InvalidFeatureQueryFormatException(query, pe);
        }
    }

    protected abstract GeneVariationPair newGeneVariationPair(String feature) throws ParseException;

    /**
     * Check that variating amino-acid(s) on isoform sequence exists and return result
     *
     * @param entryIsoform the entry isoform to check variating amino-acids
     * @param variation the variation on which expected changing amino-acids is found
     */
    private FeatureQueryResult checkFeatureOnIsoform(FeatureQuery query, EntryIsoform entryIsoform,
                                                     SequenceVariation variation) throws FeatureQueryException {

        checkIsoformPos(entryIsoform.getIsoform(), variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()), query);

        checkIsoformPos(entryIsoform.getIsoform(), variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()), query);

        FeatureQuerySuccess result = new FeatureQuerySuccess(query);

        result.setSequenceVariation(entryIsoform, variation);

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
