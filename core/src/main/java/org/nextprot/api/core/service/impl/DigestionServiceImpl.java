package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.LengthDigestionController;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DigestionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
class DigestionServiceImpl implements DigestionService {

	private static final Log LOGGER = LogFactory.getLog(DigestionServiceImpl.class);

	private AnnotationService annotationService;
	private IsoformService isoformService;
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	public DigestionServiceImpl(AnnotationService annotationService, IsoformService isoformService, MasterIdentifierService masterIdentifierService) {

		this.annotationService = annotationService;
		this.isoformService = isoformService;
		this.masterIdentifierService = masterIdentifierService;
	}

	@Override
	public Set<String> digest(String entryAccession, Protease protease, int minpeplen, int maxpeplen, int missedCleavage) {

		ProteinDigester digester = new ProteinDigester.Builder(protease)
				.controller(new LengthDigestionController(minpeplen, maxpeplen))
				.missedCleavageMax(missedCleavage)
				.build();

		List<Peptide> peptides = new ArrayList<>();

		digestProtein(entryAccession, digester, peptides);

		return peptides.stream()
				.map(peptide -> peptide.toSymbolString())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<String> digestAllWithTrypsin() {

		ProteinDigester digester = new ProteinDigester.Builder(Protease.TRYPSIN)
				.controller(new LengthDigestionController(7, 77))
				.missedCleavageMax(2)
				.build();

		Set<String> allEntries = masterIdentifierService.findUniqueNames();
		List<Peptide> allPeptides = new ArrayList<>();
		int ecnt = 0;

		LOGGER.info("Digesting " + allEntries.size() + " entries...");
		for (String entryAccession : allEntries) {

			digestProtein(entryAccession, digester, allPeptides);

			if(ecnt++ % 100 == 0) {
				LOGGER.info(ecnt + " entries so far...");
			}
		}

		return allPeptides.stream()
				.map(peptide -> peptide.toSymbolString())
				.collect(Collectors.toSet());
	}

	@Override
	public Protease[] getProteases() {

		return Protease.values();
	}

	private void digestProtein(String entryAccession, ProteinDigester digester, List<Peptide> peptides) {

		// We digest mature chains and propeptides
		annotationService.findAnnotations(entryAccession).parallelStream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MATURE_PROTEIN ||
						annotation.getAPICategory() == AnnotationCategory.MATURATION_PEPTIDE)
				.forEach(annotation -> {

					for (String isoformAccession : annotation.getTargetingIsoformsMap().keySet()) {

						Integer start = annotation.getStartPositionForIsoform(isoformAccession);
						Integer end = annotation.getEndPositionForIsoform(isoformAccession);

						if (start != null && end != null) {

							Isoform isoform = isoformService.findIsoformByName(entryAccession, isoformAccession);
							String isoformSequence = isoform.getSequence();

							digester.digest(new Protein(isoformAccession, isoformSequence.substring(start-1, end)), peptides);
						}
					}
				});
	}
}
