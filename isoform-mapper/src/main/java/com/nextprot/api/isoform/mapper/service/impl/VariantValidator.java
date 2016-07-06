package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.*;
import com.nextprot.api.isoform.mapper.domain.impl.IncompatibleGeneAndProteinNameException;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidFeatureQueryAminoAcidException;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidFeatureQueryFormatException;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidFeatureQueryPositionException;
import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantPair;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.AminoAcidCode;
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
            GeneVariantPair geneVariantPair = new GeneVariantPair(query.getFeature());

            if (!geneVariantPair.isValidGeneName(entryIsoform.getEntry())) {

                List<String> expectedGeneNames = entryIsoform.getEntry().getOverview().getGeneNames().stream()
                        .map(EntityName::getName).collect(Collectors.toList());

                throw new IncompatibleGeneAndProteinNameException(query, geneVariantPair.getGeneName(), expectedGeneNames);
            }
            return checkFeatureOnIsoform(query, entryIsoform.getIsoform(), geneVariantPair.getFeature());

        } catch (ParseException e) {

            String geneName = GeneVariantPair.getGeneName(query.getFeature());

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() + geneName.length() + 1);
            throw new InvalidFeatureQueryFormatException(query, pe);
        }
    }

    /**
     * Check that variating amino-acid(s) on isoform sequence exists and return result
     *
     * @param isoform the isoform to check variating amino-acids
     * @param variant the variation on which expected changing amino-acids is found
     */
    private FeatureQueryResult checkFeatureOnIsoform(FeatureQuery query, Isoform isoform,
                                                     IsoformFeature variant) throws FeatureQueryException {

        checkIsoformPos(isoform, variant.getFirstChangingAminoAcidPos(),
                String.valueOf(variant.getFirstChangingAminoAcid().get1LetterCode()), query);

        checkIsoformPos(isoform, variant.getLastChangingAminoAcidPos(),
                String.valueOf(variant.getLastChangingAminoAcid().get1LetterCode()), query);

        FeatureQueryResult result = new FeatureQuerySuccess(query);

        ((FeatureQuerySuccess) result).addMappedFeature(isoform,
                variant.getFirstChangingAminoAcidPos(), variant.getLastChangingAminoAcidPos());

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
