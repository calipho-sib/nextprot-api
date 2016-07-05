package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.impl.*;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureFailure;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Validate variant type features
 *
 * Created by fnikitin on 05/07/16.
 */
class VariantValidator implements FeatureValidator {

    @Override
    public MappedIsoformsFeatureResult validate(FeatureQuery query, EntryIsoform entryIsoform) {

        try {
            GeneVariantSplitter splitter = new GeneVariantSplitter(query.getFeature());
            if (!splitter.isValidGeneName(entryIsoform.getEntry())) {

                List<String> expectedGeneNames = entryIsoform.getEntry().getOverview().getGeneNames().stream()
                        .map(EntityName::getName).collect(Collectors.toList());

                return new IncompatibleGeneAndProteinNameFailure(query, splitter.getGeneName(), expectedGeneNames);
            }

            ProteinSequenceVariation entryIsoformVariation = splitter.getVariant();

            return checkFeatureOnIsoform(query, entryIsoform.getIsoform(), entryIsoformVariation);
        } catch (ParseException e) {

            String geneName = GeneVariantSplitter.getGeneName(query.getFeature());

            ParseException pe = new ParseException(e.getMessage(), e.getErrorOffset() + geneName.length() + 1);
            return new InvalidFeatureFormatFailure(query, pe);
        }
    }

    /**
     * Check that variating amino-acid(s) on isoform sequence exists and return result
     *
     * @param isoform   the isoform to check variating amino-acids
     * @param variation the variation on which expected changing amino-acids is found
     */
    private MappedIsoformsFeatureResult checkFeatureOnIsoform(FeatureQuery query, Isoform isoform,
                                                              ProteinSequenceVariation variation) {

        Optional<MappedIsoformsFeatureFailure> firstPosError = checkInvalidIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()), query);

        Optional<MappedIsoformsFeatureFailure> lastPosError = checkInvalidIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()), query);

        // invalid
        if (firstPosError.isPresent()) {
            return firstPosError.get();
        } else if (lastPosError.isPresent()) {
            return lastPosError.get();
        }

        // valid feature
        else {
            MappedIsoformsFeatureResult result = new MappedIsoformsFeatureSuccess(query);

            ((MappedIsoformsFeatureSuccess) result).addMappedIsoformFeature(isoform.getUniqueName(),
                    variation.getFirstChangingAminoAcidPos(), variation.getLastChangingAminoAcidPos());

            return result;
        }
    }

    /**
     * @param isoform
     * @param position
     * @param aas
     * @return an ErrorValue if invalid else null
     */
    private Optional<MappedIsoformsFeatureFailure> checkInvalidIsoformPos(Isoform isoform, int position, String aas, FeatureQuery query) {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            return Optional.of(new InvalidFeaturePositionFailure(query, position));
        }

        if (!insertionMode) {
            valid = IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas);

            if (!valid) {

                String aasOnSequence = isoform.getSequence().substring(position - 1, position + aas.length() - 1);

                return Optional.of(new InvalidFeatureAminoAcidFailure(query, position,
                        AminoAcidCode.valueOfOneLetterCodeSequence(aasOnSequence),
                        AminoAcidCode.valueOfOneLetterCodeSequence(aas)));
            }
        }

        return Optional.empty();
    }
}
