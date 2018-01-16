package org.nextprot.api.core.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

		if (cannotBePromoted(entryProperties)) {
			return new ProteinExistenceWithRule(entryProperties.getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT),
					ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_01);
		}
		if (upgradeAccordingToRule2(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_02);
		}
		if (upgradeAccordingToRule3(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_03);
		}
		if (upgradeAccordingToRule4(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_02);
		}
		if (upgradeAccordingToRule5(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_05);
		}
		if (upgradeAccordingToRule6(annots)) {

			return new ProteinExistenceWithRule(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceWithRule.ProteinExistenceRule.SP_PER_06);
		}

		return null;
	}

	// Rules defined here:
	//https://swissprot.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules

	private boolean cannotBePromoted(EntryProperties entryProperties) {

		ProteinExistence pe = entryProperties.getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

		return pe == ProteinExistence.PROTEIN_LEVEL || pe == ProteinExistence.UNCERTAIN;
	}

	private boolean upgradeAccordingToRule2(List<Annotation> annots) {

		return false;
	}

	private boolean upgradeAccordingToRule3(List<Annotation> annots) {

		return false;
	}

	private boolean upgradeAccordingToRule4(List<Annotation> annots) {

		return false;
	}

	private boolean upgradeAccordingToRule5(List<Annotation> annots) {

		return false;
	}

	private boolean upgradeAccordingToRule6(List<Annotation> annots) {

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
