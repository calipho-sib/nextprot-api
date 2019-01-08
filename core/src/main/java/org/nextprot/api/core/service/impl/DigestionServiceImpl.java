package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.LengthDigestionController;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DigestionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
	public Set<String> digest(String entryAccession, String proteaseName, int minpeplen, int maxpeplen, int missedCleavageCount) {

		if (missedCleavageCount < 0) {
			throw new NextProtException("number of missed cleavages should be positive.");
		}
		else if (missedCleavageCount > 2) {
			throw new NextProtException(missedCleavageCount+" missed cleavages is too high: cannot configure digestion with more than 2 missed cleavages.");
		}

		if (maxpeplen <= 0) {
			throw new NextProtException("max peptide length should be greater than 1.");
		}

		ProteinDigester digester = new ProteinDigester.Builder(getProtease(proteaseName))
				.controller(new LengthDigestionController(minpeplen, maxpeplen))
				.missedCleavageMax(missedCleavageCount)
				.build();

		List<Peptide> peptides = new ArrayList<>();

		digestProtein(entryAccession, digester, peptides);

		return peptides.stream()
				.map(peptide -> peptide.toSymbolString())
				.collect(Collectors.toSet());
	}

	@Cacheable("all-tryptic-digests")
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
	public List<String> getProteaseNames() {

		return Stream.of(Protease.values())
				.map(protease -> getProteaseNameWithoutTypo(protease))
				.sorted()
				.collect(Collectors.toList());
	}

	private void digestProtein(String entryAccession, ProteinDigester digester, List<Peptide> peptides) {

		// We digest mature chains and propeptides
		annotationService.findAnnotations(entryAccession).stream()
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

	private Protease getProtease(String proteaseName) {

		if ("PEPSIN_PH_1_3".equals(proteaseName)) {
			return Protease.PEPSINE_PH_1_3;
		}
		else if ("PEPSIN_PH_GT_2".equals(proteaseName)) {
			return Protease.PEPSINE_PH_GT_2;
		}
		else if ("THERMOLYSIN".equals(proteaseName)) {
			return Protease.THERMOLYSINE;
		}
		return Protease.valueOf(proteaseName);
	}

	private String getProteaseNameWithoutTypo(Protease protease) {

		if (protease == Protease.PEPSINE_PH_1_3) {
			return "PEPSIN_PH_1_3";
		}
		else if (protease == Protease.PEPSINE_PH_GT_2) {
			return "PEPSIN_PH_GT_2";
		}
		else if (protease == Protease.THERMOLYSINE) {
			return "THERMOLYSIN";
		}
		return protease.name();
	}
}
