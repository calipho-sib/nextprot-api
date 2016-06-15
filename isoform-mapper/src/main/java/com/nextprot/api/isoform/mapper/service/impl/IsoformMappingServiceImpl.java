package com.nextprot.api.isoform.mapper.service.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    public OverviewService overviewService;

    @Override
    public IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate) {

        // only variant by now (TODO: ptm to implement)
        Preconditions.checkArgument(annotationCategory == AnnotationCategory.VARIANT);

        IsoformFeatureMapping mapping = new IsoformFeatureMapping();

        try {
            GeneNameAndProteinMutation mutation = GeneNameAndProteinMutation.parseHGV(featureName);

            //1) Validate
            if (validate(nextprotAccession, mutation)) {

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

    private boolean validate(String nextprotAccession, GeneNameAndProteinMutation mutation) {

        // 1. get overview of entry, check gene name is as expected
        Overview overview = overviewService.findOverviewByEntry(nextprotAccession);

        // TODO: check if genename contained in gene name list instead
        if (!overview.getMainGeneName().equals(mutation.getGeneName()))
            return false;

        // 2. check AA exists in isoform at specified position

        return true;
    }

    private void propagate() {

    }

    private void checkRules() {

    }

    static class GeneNameAndProteinMutation {

        private final String geneName;
        private final ProteinMutation proteinMutation;
        private final static ProteinMutationHGVFormat PROTEIN_MUTATION_HGV_FORMAT = new ProteinMutationHGVFormat();

        GeneNameAndProteinMutation(String geneName, ProteinMutation proteinMutation) {
            this.geneName = geneName;
            this.proteinMutation = proteinMutation;
        }

        static GeneNameAndProteinMutation parseHGV(String mutation) throws ParseException {

            String[] geneNameAndHGV = mutation.split("-");

            String geneName = geneNameAndHGV[0];
            String hgvProtein = geneNameAndHGV[1];

            ProteinMutation proteinMutation = PROTEIN_MUTATION_HGV_FORMAT.parse(hgvProtein, AbstractProteinMutationFormat.ParsingMode.PERMISSIVE);

            return new GeneNameAndProteinMutation(geneName, proteinMutation);
        }

        public String getGeneName() {
            return geneName;
        }

        public ProteinMutation getProteinMutation() {
            return proteinMutation;
        }
    }
}
