package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.bio.variation.prot.digestion.AminoAcidSequenceDigestion;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteaseAdapter;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigesterBuilder;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigestion;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DigestionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
	public Set<String> digestProteins(String isoformOrEntryAccession, ProteinDigesterBuilder builder) throws ProteinDigestion.MissingIsoformException {

		if (IsoformUtils.isIsoformAccession(isoformOrEntryAccession)) {
			return digestIsoforms(Collections.singletonList(isoformOrEntryAccession), builder);
		}
		else {
			return digestIsoforms(isoformService.findIsoformsByEntryName(isoformOrEntryAccession).stream()
					.map(isoform -> isoform.getIsoformAccession()).collect(Collectors.toList()), builder);
		}
	}

	private Set<String> digestIsoforms(List<String> isoformAccessions, ProteinDigesterBuilder builder) throws ProteinDigestion.MissingIsoformException {

		List<Peptide> peptides = new ArrayList<>();

		ProteinDigestion digestion = (builder.withMaturePartsOnly()) ?
				new MatureSequencesDigestion(builder.build()) : new WholeSequenceDigestion(builder.build());

		for (String isoformAccession : isoformAccessions) {

			digestion.digest(isoformAccession, peptides);
		}

		return peptides.stream()
				.map(peptide -> peptide.toSymbolString())
				.collect(Collectors.toSet());
	}

	@Cacheable("all-tryptic-digests")
	@Override
	public Set<String> digestAllMatureProteinsWithTrypsin() {

		ProteinDigestion digestion = new MatureSequencesDigestion(new ProteinDigesterBuilder().build());

		Set<String> allEntries = masterIdentifierService.findUniqueNames();
		List<Peptide> peptides = new ArrayList<>();
		int processedEntries = 0;

		LOGGER.info("Digesting mature proteins of " + allEntries.size() + " neXtProt entries...");
		for (String entryAccession : allEntries) {

			isoformService.findIsoformsByEntryName(entryAccession)
					.forEach(isoform -> {
						try {
							digestion.digest(isoform.getIsoformAccession(), peptides);
						} catch (ProteinDigestion.MissingIsoformException e) {
							throw new NextProtException(e);
						}
					});

			if (processedEntries++ % 100 == 0) {
				LOGGER.info(processedEntries + " entries processed");
			}
		}

		return peptides.stream()
				.map(peptide -> peptide.toSymbolString())
				.collect(Collectors.toSet());
	}

	@Override
	public List<String> getProteaseNames() {

		return new ProteaseAdapter().getProteaseNames();
	}

	private class MatureSequencesDigestion extends AminoAcidSequenceDigestion {

		private MatureSequencesDigestion(ProteinDigester digester) {
			super(digester);
		}

		@Override
		public List<String> getIsoformSequences(String isoformAccession) throws MissingIsoformException {

			Isoform isoform = isoformService.findIsoform(isoformAccession);

			if (isoform == null) {

				throw new MissingIsoformException(isoformAccession);
			}

			String entryAccession = IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession(isoformAccession);
			String sequence = isoform.getSequence();

			return annotationService.findAnnotations(entryAccession).stream()
					.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MATURE_PROTEIN ||
							annotation.getAPICategory() == AnnotationCategory.MATURATION_PEPTIDE)
					.filter(annotation -> annotation.isAnnotationPositionalForIsoform(isoformAccession))
					.flatMap(annotation -> {
							Integer start = annotation.getStartPositionForIsoform(isoformAccession);
							Integer end = annotation.getEndPositionForIsoform(isoformAccession);

							if (start != null && end != null) {

								return Stream.of(sequence.substring(start - 1, end));
							}
							return Stream.empty();
						}
					)
					.collect(Collectors.toList());
		}
	}

	private class WholeSequenceDigestion extends AminoAcidSequenceDigestion {

		private WholeSequenceDigestion(ProteinDigester digester) {
			super(digester);
		}

		@Override
		public List<String> getIsoformSequences(String isoformAccession) throws MissingIsoformException {

			Isoform isoform = isoformService.findIsoform(isoformAccession);

			if (isoform == null) {

				throw new MissingIsoformException(isoformAccession);
			}

			return Collections.singletonList(isoform.getSequence());
		}
	}
}
