package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.dao.ProteinExistenceDao;
import org.nextprot.api.core.domain.CvTermGraph;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.ProteinExistenceInferred;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.ProteinExistenceInferenceService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * See specifications on https://calipho.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules
 */
@Service
class ProteinExistenceInferenceServiceImpl implements ProteinExistenceInferenceService {

	@Autowired
	private ProteinExistenceDao proteinExistenceDao;

	@Autowired
	private TerminologyService terminologyService;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private CvTermGraphService cvTermGraphService;

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

			return new ProteinExistenceInferred(ProteinExistence.TRANSCRIPT_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_04);
		}
		if (promotedAccordingToRule5(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_05);
		}
		if (promotedAccordingToRule6(entryAccession)) {

			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_06);
		}
        if (promotedAccordingToRule7(entryAccession)) {

            return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_07);
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

        List<Annotation> filteredPeptideMappingList =
				annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.PEPTIDE_MAPPING)
				.filter(AnnotationUtils::isProteotypicPeptideMapping)
	            .filter(pm -> pm.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
	            .collect(Collectors.toList());
        return AnnotationUtils.containsAtLeast2NonInclusivePeptidesMinSize9Coverage18(filteredPeptideMappingList);
        
	}

	// Spec: Entry must have an expression information annotation containing the text "(at protein level)"
    // with evidence assigned by neXtProt of quality GOLD AND ECO experimental evidence (or child thereof)
	public boolean promotedAccordingToRule3(String entryAccession) {

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(() -> annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.EXPRESSION_INFO)
				.filter(ei -> ei.getDescription().contains("(at protein level)")));
	}

    // Spec: Entry having PE3 (HOMOLOGY) or PE4 (PREDICTED) must have an expression profile annotation with evidence
    // assigned by HPA of quality GOLD with ECO:0000295 (RNA-seq evidence) and expression level High or Medium
	@Override
	public boolean promotedAccordingToRule4(String entryAccession) {

        ProteinExistence pe = proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

        if (pe == ProteinExistence.HOMOLOGY || pe == ProteinExistence.PREDICTED) {

            return annotationService.findAnnotations(entryAccession).stream()
					.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.EXPRESSION_PROFILE)
                    .flatMap(annot -> annot.getEvidences().stream())
                    .filter(evidence -> "Human protein atlas".equals(evidence.getAssignedBy()))
                    .filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                    .filter(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 85109))
                    .anyMatch(evidence -> evidence.isExpressionLevelDetected(Arrays.asList("high", "medium")));
        }

		return false;
	}

	// Spec: Entry must have a mutagenesis annotation with evidence assigned by neXtProt of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule5(String entryAccession) {

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(() -> annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MUTAGENESIS));
	}

	// Spec: Entry must have a binary interaction annotation with evidence assigned by neXtProt of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule6(String entryAccession) {

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(() -> annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.BINARY_INTERACTION));
	}

    // Spec: Entry must have a modified residue annotation with evidence of quality GOLD and AND ECO experimental evidence (or child thereof)
    // other than mass spectrometry evidence (ECO:0001096)
    @Override
    public boolean promotedAccordingToRule7(String entryAccession) {

        return annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE)
                .flatMap(annot -> annot.getEvidences().stream())
                .filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                .filter(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 84877))
                .anyMatch(evidence -> !isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 154119));
    }

	// Term "experimental evidence": AC=ECO:0000006, ID=84877
    private boolean hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(Supplier<Stream<Annotation>> streamSupplier) {

		return streamSupplier.get().flatMap(annot -> annot.getEvidences().stream())
				.filter(evidence -> "NextProt".equals(evidence.getAssignedBy()))
				.filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
				.anyMatch(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 84877));
	}

    private boolean isChildOfEvidenceTerm(String evidenceCodeAC, int evidenceCodeIdAncestor) {

        CvTermGraph evidenceCodeTermGraph = cvTermGraphService.findCvTermGraph(TerminologyCv.EvidenceCodeOntologyCv);
        int termId = terminologyService.findCvTermByAccessionOrThrowRuntimeException(evidenceCodeAC).getId().intValue();

        return evidenceCodeIdAncestor == termId || evidenceCodeTermGraph.isDescendantOf(termId, evidenceCodeIdAncestor);
    }
}
