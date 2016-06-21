package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;


/**
 * Specs: https://issues.isb-sib.ch/browse/BIOEDITOR-397
 */
@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Override
    public IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate) {

        NextprotEntry nextprotEntry = NextprotEntry.parseAccession(nextprotAccession, entryBuilderService);

        switch (annotationCategory) {
            case VARIANT:
                return validateVariant(featureName, nextprotEntry, propagate);
            case PTM_INFO:
                return validatePtm(featureName, nextprotEntry, propagate);
            default:
                throw new IllegalArgumentException("cannot handle annotation category " + annotationCategory);
        }
    }

    private IsoformFeatureMapping validateVariant(String featureName, NextprotEntry nextprotEntry, boolean propagate) {

        IsoformFeatureMapping mapping = new IsoformFeatureMapping();

        try {
            GeneVariantParser parser = new GeneVariantParser(featureName, nextprotEntry.getEntry());
            ProteinSequenceVariation variation = parser.getProteinSequenceVariation();
            String geneName = parser.getGeneName();

            //1) Validate
            if (validateIsoformPosition(variation, nextprotEntry.getEntry())) {

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

    private IsoformFeatureMapping validatePtm(String featureName, NextprotEntry nextprotEntry, boolean propagate) {

        throw new IllegalStateException("ptm validation not yet implemented");
    }

    private boolean validateIsoformPosition(ProteinSequenceVariation variation, Entry entry) {

        // 2. check AA exists in isoform at specified position
        Propagator propagator = new Propagator(entry);


        return true;
    }

    private void propagate() {

    }

    private void checkRules() {

    }

    private static class NextprotEntry {

        private final Entry entry;
        private final String isoformAccession;

        private NextprotEntry(Entry entry, String isoformAccession) {

            this.entry = entry;
            this.isoformAccession = isoformAccession;
        }

        public static NextprotEntry parseAccession(String accession, EntryBuilderService entryBuilderService) {

            String entryAccession;
            String isoformAccession = null;

            if (accession.contains("-")) {
                int colonPosition = accession.indexOf("-");
                entryAccession = accession.substring(0, colonPosition);
                isoformAccession = accession.substring(colonPosition);


            } else {
                entryAccession = accession;
            }

            Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything());

            return new NextprotEntry(entry, isoformAccession);
        }

        public Entry getEntry() {
            return entry;
        }

        public String getIsoformAccession() {
            return isoformAccession;
        }
    }
}
