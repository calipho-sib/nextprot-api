package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.ProteinExistenceService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProteinExistenceServiceImpl implements ProteinExistenceService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public boolean upgrade(String entryAccession) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything());

        //return wouldUpgradeToPE1AccordingToOldRule(entry);

        if (upgradeAccordingToRule1(entry)) {
            return true;
        }
        if (upgradeAccordingToRule2(entry)) {
            return true;
        }
        if (upgradeAccordingToRule3(entry)) {
            return true;
        }
        if (upgradeAccordingToRule4(entry)) {
            return true;
        }
        if (upgradeAccordingToRule5(entry)) {
            return true;
        }
        if (upgradeAccordingToRule6(entry)) {
            return true;
        }
        return false;
    }

    // Rules defined here:
    //https://swissprot.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules

    private boolean upgradeAccordingToRule1(Entry entry) {

        return false;
    }

    private boolean upgradeAccordingToRule2(Entry entry) {

        return false;
    }

    private boolean upgradeAccordingToRule3(Entry entry) {

        return false;
    }

    private boolean upgradeAccordingToRule4(Entry entry) {

        return false;
    }

    private boolean upgradeAccordingToRule5(Entry entry) {

        return false;
    }

    private boolean upgradeAccordingToRule6(Entry entry) {

        return false;
    }

    // Is this code (coming from EntryUtils) is the NP1 rule ?
    @SuppressWarnings("Duplicates")
    private boolean wouldUpgradeToPE1AccordingToOldRule(Entry e) {

        if (e.getProteinExistence()== ProteinExistence.PROTEIN_LEVEL) return false; // already PE1
        if (e.getProteinExistence()== ProteinExistence.UNCERTAIN) return false; // we don't upgrade PE5
        if (! e.getAnnotationsByCategory().containsKey("peptide-mapping")) return false; // no peptide mapping, no chance to upgrade to PE1
        List<Annotation> list = e.getAnnotationsByCategory().get("peptide-mapping").stream()
                .filter(a -> AnnotationUtils.isProteotypicPeptideMapping(a)).collect(Collectors.toList());
        if (list==null) return false;
        if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 2, 7)) return true;
        if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 1, 9)) return true;
        return false;
    }
}