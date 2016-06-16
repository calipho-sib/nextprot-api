package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Override
    public IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate) {

        if (annotationCategory == AnnotationCategory.VARIANT)
            return validateVariant(featureName, nextprotAccession, propagate);
        else if (annotationCategory == AnnotationCategory.PTM_INFO)
            throw new IllegalStateException("ptm validation not yet implemented");
        else
            throw new IllegalArgumentException("cannot handle annotation category "+annotationCategory);
    }

    public IsoformFeatureMapping validateVariant(String featureName, String nextprotAccession, boolean propagate) {

        IsoformFeatureMapping mapping = new IsoformFeatureMapping();

        try {
            ProteinMutationBuilder builder = new ProteinMutationBuilder(featureName, nextprotAccession, overviewService);
            ProteinMutation mutation = builder.getProteinMutation();

            //1) Validate
            if (validateIsoformPosition(mutation)) {

                //2) propagate if flag = true returns a map with N isoforms
                if (propagate) {

                    propagate();
                    //3) check rules
                    checkRules();
                }

            } else {
                // not validated
                // error message...

            }
        } catch (ParseException e) {

            throw new RuntimeException(featureName + ": invalid feature name", e);
        }

        return mapping;
    }

    private boolean validateIsoformPosition(ProteinMutation mutation) {

        // 2. check AA exists in isoform at specified position

        return true;
    }

    private void propagate() {

    }

    private void checkRules() {

    }

    private static class ProteinMutationBuilder {

        private final String geneName;
        private final ProteinMutation proteinMutation;
        private final ProteinMutationHGVFormat PROTEIN_MUTATION_HGV_FORMAT = new ProteinMutationHGVFormat();

        ProteinMutationBuilder(String mutation, String nextprotAccession, OverviewService overviewService) throws ParseException {

            String[] geneNameAndHGV = mutation.split("-");

            if (!validateGeneName(nextprotAccession, geneNameAndHGV[0], overviewService)) {
                throw new NextProtException(nextprotAccession + " does not comes from gene " + geneNameAndHGV[0]);
            }

            proteinMutation = PROTEIN_MUTATION_HGV_FORMAT.parse(geneNameAndHGV[1], AbstractProteinMutationFormat.ParsingMode.PERMISSIVE);
            geneName = geneNameAndHGV[0];
        }

        private boolean validateGeneName(String nextprotAccession, String geneName, OverviewService overviewService) {

            // 1. get overview of entry, check gene name is as expected
            Overview overview = overviewService.findOverviewByEntry(nextprotAccession);

            // TODO: check if genename contained in gene name list instead
            if (!overview.getMainGeneName().equals(geneName))
                return false;
            return true;
        }

        public ProteinMutation getProteinMutation() {
            return proteinMutation;
        }
    }
}
