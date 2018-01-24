package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.dao.ProteinExistenceDao;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.ProteinExistenceInferred;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.ProteinExistenceInferenceService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.api.core.utils.graph.CvTermGraph;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
class ProteinExistenceInferenceServiceImpl implements ProteinExistenceInferenceService {

	@Autowired
	private ProteinExistenceDao proteinExistenceDao;

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private TerminologyService terminologyService;

	@Override
	public ProteinExistenceInferred inferProteinExistence(String entryAccession) {

		if (cannotBePromotedAccordingToRule1(entryAccession)) {
			return new ProteinExistenceInferred(proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT),
					ProteinExistenceInferred.ProteinExistenceRule.SP_PER_01);
		}
		if (promotedAccordingToRule2(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_02);
		}
		if (promotedAccordingToRule3(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_03);
		}
		if (promotedAccordingToRule4(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_04);
		}
		if (promotedAccordingToRule5(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_05);
		}
		if (promotedAccordingToRule6(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_06);
		}

		return ProteinExistenceInferred.noInferenceFound(proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT));
	}

	// Rules defined here:
	//https://swissprot.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules
	@Override
	public boolean cannotBePromotedAccordingToRule1(String entryAccession) {

		ProteinExistence pe = proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

		return pe == ProteinExistence.PROTEIN_LEVEL || pe == ProteinExistence.UNCERTAIN;
	}

	// Spec: Entry must have at least 2 proteotypic peptides of quality GOLD, 9 or more amino acids in length,
    // which must differ by at least 1 amino acid and not overlap (i.e. one of the peptides must not be included in the other)
	@Override
	public boolean promotedAccordingToRule2(String entryAccession) {

        List<Annotation> filteredPeptideMappingList = new ArrayList<>();

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

		entry.getAnnotationsByCategory(AnnotationCategory.PEPTIDE_MAPPING).stream()
				.filter(AnnotationUtils::isProteotypicPeptideMapping)
                .filter(pm -> pm.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                .forEach(pm -> AnnotationUtils.addToNonInclusivePeptideMappingList(pm, filteredPeptideMappingList, 9));

		return filteredPeptideMappingList.size() > 1;
	}

	// Spec: Entry must have an expression information annotation containing the text "(at protein level)"
    // with evidence assigned by neXtProt of quality GOLD AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule3(String entryAccession) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(entry.getAnnotationsByCategory(AnnotationCategory.EXPRESSION_INFO).stream()
				.filter(ei -> ei.getDescription().contains("(at protein level)")));
	}

    // Spec: Entry having PE3 (HOMOLOGY) or PE4 (PREDICTED) must have an expression profile annotation with evidence
    // assigned by HPA of quality GOLD with ECO:0000295 (RNA-seq evidence) and expression level High or Medium
	@Override
	public boolean promotedAccordingToRule4(String entryAccession) {

        ProteinExistence pe = proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

        if (pe == ProteinExistence.HOMOLOGY || pe == ProteinExistence.PREDICTED) {

            Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

            return entry.getAnnotationsByCategory(AnnotationCategory.EXPRESSION_PROFILE).stream()
                    .flatMap(annot -> annot.getEvidences().stream())
                    .filter(evidence -> "Human protein atlas".equals(evidence.getAssignedBy()))
                    .filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                    .filter(evidence -> isChildOfExperimentalEvidenceTerm(evidence.getEvidenceCodeAC(), 85109))
                    .anyMatch(evidence -> evidence.isExpressionLevelDetected(Arrays.asList("high", "medium")));
        }

		return false;
	}

	// Spec: Entry must have a mutagenesis annotation with evidence assigned by neXtProt of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule5(String entryAccession) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(entry.getAnnotationsByCategory(AnnotationCategory.MUTAGENESIS).stream());
	}

	// Spec: Entry must have a binary interaction annotation with evidence assigned by neXtProt of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule6(String entryAccession) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(entry.getAnnotationsByCategory(AnnotationCategory.BINARY_INTERACTION).stream());
	}

	private boolean isChildOfExperimentalEvidenceTerm(String evidenceCodeAC, int evidenceCodeACAncestor) {

		CvTermGraph evidenceCodeTermGraph = terminologyService.findCvTermGraph(TerminologyCv.EvidenceCodeOntologyCv);
		int termId = terminologyService.findCvTermByAccession(evidenceCodeAC).getId().intValue();

		return evidenceCodeACAncestor == termId || evidenceCodeTermGraph.isDescendantOf(termId, evidenceCodeACAncestor);
	}

	private boolean hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(Stream<Annotation> stream) {

		return stream.flatMap(annot -> annot.getEvidences().stream())
				.filter(evidence -> "NextProt".equals(evidence.getAssignedBy()))
				.filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
				.anyMatch(evidence -> isChildOfExperimentalEvidenceTerm(evidence.getEvidenceCodeAC(), 84877));

		// { "id" : 84877, "accession" : "ECO:0000006", "name" : "experimental evidence" }
	}
}
