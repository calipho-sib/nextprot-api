package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.EntryPropertiesDao;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.ProteinExistenceWithRule;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryPropertiesService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
class EntryPropertiesServiceImpl implements EntryPropertiesService {

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private EntryPropertiesDao entryPropertiesDao;

	@Autowired
	private AnnotationService annotationService;

	@Override
	@Cacheable("entry-properties")
	public EntryProperties findEntryProperties(String uniqueName) {

		EntryProperties entryProperties = entryPropertiesDao.findEntryProperties(uniqueName);

		// see https://issues.isb-sib.ch/browse/NEXTPROT-1512
		entryProperties.setProteinExistenceWithRule(calcNextprotProteinExistence(uniqueName, entryProperties));

		return entryProperties;
	}

	@Override
	public boolean proteinExistencePromoted(String entryAccession) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything());
		return wouldUpgradeToPE1AccordingToOldRule(entry);
	}

	private ProteinExistenceWithRule calcNextprotProteinExistence(String entryAccession, EntryProperties entryProperties) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations().withoutProperties());
		List<Annotation> annots = entry.getAnnotations();

		if (cannotBePromotedAccordingToRule1(entryProperties)) {
			return new ProteinExistenceWithRule(entryProperties.getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT),
					ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_01);
		}
		if (promotedAccordingToRule2(entry.getAnnotationsByCategory(AnnotationCategory.PEPTIDE_MAPPING))) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_02);
		}
		if (promotedAccordingToRule3(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_03);
		}
		if (promotedAccordingToRule4(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_02);
		}
		if (promotedAccordingToRule5(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_05);
		}
		if (promotedAccordingToRule6(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_06);
		}

		return null;
	}

	// Rules defined here:
	//https://swissprot.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules

	private boolean cannotBePromotedAccordingToRule1(EntryProperties entryProperties) {

		ProteinExistence pe = entryProperties.getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

		return pe == ProteinExistence.PROTEIN_LEVEL || pe == ProteinExistence.UNCERTAIN;
	}

    // Spec: Entry must have at least 2 proteotypic peptides of quality GOLD, 9 or more amino acids in length,
    // which must differ by at least 1 amino acid and not overlap (i.e. one of the peptides must not be included in the other)
    private boolean promotedAccordingToRule2(List<Annotation> peptideMappingAnnots) {

        List<Annotation> filteredPeptideMappingList = new ArrayList<>();

        peptideMappingAnnots.stream()
                .filter(pm -> pm.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                .forEach(pm -> AnnotationUtils.addToNonInclusivePeptideMappingList(pm, filteredPeptideMappingList, 9));

		return filteredPeptideMappingList.size() > 1;
	}

	private boolean promotedAccordingToRule3(List<Annotation> annots) {

		return false;
	}

	private boolean promotedAccordingToRule4(List<Annotation> annots) {

		return false;
	}

	private boolean promotedAccordingToRule5(List<Annotation> annots) {

		return false;
	}

	private boolean promotedAccordingToRule6(List<Annotation> annots) {

		return false;
	}

	// Is this code (coming from EntryUtils) is the NP1 rule ?
	@SuppressWarnings("Duplicates")
	private boolean wouldUpgradeToPE1AccordingToOldRule(Entry e) {

		ProteinExistence pe = e.getProperties().getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

		if (pe== ProteinExistence.PROTEIN_LEVEL) return false; // already PE1
		if (pe== ProteinExistence.UNCERTAIN) return false; // we don't proteinExistencePromoted PE5
		if (! e.getAnnotationsByCategory().containsKey("peptide-mapping")) return false; // no peptide mapping, no chance to proteinExistencePromoted to PE1
		List<Annotation> list = e.getAnnotationsByCategory().get("peptide-mapping").stream()
				.filter(a -> AnnotationUtils.isProteotypicPeptideMapping(a)).collect(Collectors.toList());
		if (list==null) return false;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 2, 7)) return true;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 1, 9)) return true;
		return false;
	}

}
