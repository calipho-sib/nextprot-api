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
import org.nextprot.api.core.service.annotation.PeptideSet;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * See specifications on https://calipho.isb-sib.ch/wiki/display/cal/Protein+existence+%28PE%29+upgrade+rules
 */
@Service
class ProteinExistenceInferenceServiceImpl implements ProteinExistenceInferenceService {

    private static final Logger LOGGER = Logger.getLogger(ProteinExistenceInferenceServiceImpl.class.getName());

	
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
			LOGGER.info("ProteinExistence: cannotBePromotedAccordingToRule1:" + entryAccession);
			return new ProteinExistenceInferred(proteinExistenceDao.findProteinExistenceUniprot(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT),
					ProteinExistenceInferred.ProteinExistenceRule.SP_PER_01);
		}
		if (promotedAccordingToRule2(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule2: " + entryAccession + " to PE1");
			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_02);
		}
		if (promotedAccordingToRule3(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule3: " + entryAccession  + " to PE1");
			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_03);
		}
		if (promotedAccordingToRule5(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule5: " + entryAccession + " to PE1");
			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_05);
		}
		if (promotedAccordingToRule6(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule6: " + entryAccession + " to PE1");
			return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_06);
		}
        if (promotedAccordingToRule7(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule7: " + entryAccession + " to PE1");
            return new ProteinExistenceInferred(ProteinExistence.PROTEIN_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_07);
        }
        
        // WARNING: this rule must be after rules promoting to PE1, they have precedence
		if (promotedAccordingToRule4(entryAccession)) {
			LOGGER.info("ProteinExistence: promotedAccordingToRule4: " + entryAccession + " to PE2");
			return new ProteinExistenceInferred(ProteinExistence.TRANSCRIPT_LEVEL, ProteinExistenceInferred.ProteinExistenceRule.SP_PER_04);
		}
		
		LOGGER.info("ProteinExistence: promotedByNoRule: " + entryAccession);
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
	// and since 2020 the 2 peptides should belong to the same peptide set
	@Override
	public boolean promotedAccordingToRule2(String entryAccession) {
		List<Annotation> annots = annotationService.findAnnotations(entryAccession);
		AnnotationUtils.Rule2Result result = AnnotationUtils.entryAnnotationsMeetProteinExistenceRule2(annots);
		if (result.success) {
			LOGGER.info("ProteinExistence: promotion using peptideSet: " + result.peptideSet + " : " + result.pairFound);
		}
		return result.success;
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
                     // we also have Bgee RNA-seq data since Oct 2020 and we currently only have Bgee and HPA as expression data sources
                     // which makes criterion at next line obsolete 
                     //.filter(evidence -> "Human protein atlas".equals(evidence.getAssignedBy()))
                    .filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                    .filter(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 85109))  // child of or equals to ECO:0000295 - RNA-seq evidence 
                    .anyMatch(evidence -> evidence.isExpressionLevelEqualTo("detected"));
        }             

		return false;
	}

	// Spec: Entry must have a mutagenesis annotation with evidence assigned by neXtProt of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule5(String entryAccession) {

		return hasExperimentalEvidenceAssignedByNeXtProtOfQualityGOLD(() -> 
			annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MUTAGENESIS));
	}

	// Spec: Entry must have a binary interaction annotation with evidence of quality GOLD
	// AND ECO experimental evidence (or child thereof)
	@Override
	public boolean promotedAccordingToRule6(String entryAccession) {

		return hasExperimentalEvidenceOfQualityGOLD(() -> annotationService.findAnnotations(entryAccession).stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.BINARY_INTERACTION));
	}

    // Spec: Entry must have a modified residue annotation with evidence of quality GOLD and ECO experimental 
	// evidence (or child thereof) other than mass spectrometry evidence (ECO:0001096)
    // Note: Term "experimental evidence"      : ECO:0000006 (ID=84877)
	// Note: Term "mass spectrometry evidence" : ECO:0001096 (ID=154119)
    @Override
    public boolean promotedAccordingToRule7(String entryAccession) {

        return annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE)
                .flatMap(annot -> annot.getEvidences().stream())
                .filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                .filter(evidence -> ! "Uniprot".equals(evidence.getAssignedBy()))
                .filter(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 84877))
                .anyMatch(evidence -> !isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 154119));
    }

    private boolean hasExperimentalEvidenceOfQualityGOLD(Supplier<Stream<Annotation>> streamSupplier) {

		return streamSupplier.get().flatMap(annot -> annot.getEvidences().stream())
				.filter(evidence -> evidence.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
				.anyMatch(evidence -> isChildOfEvidenceTerm(evidence.getEvidenceCodeAC(), 84877));
	}

    // Note: Term "experimental evidence": ECO:0000006 (ID=84877)
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
