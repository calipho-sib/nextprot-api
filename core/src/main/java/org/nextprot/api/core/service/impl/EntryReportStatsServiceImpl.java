package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.PublicationCategory;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.nextprot.api.core.service.*;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nextprot.api.commons.utils.StreamUtils.nullableListToStream;

@Service
public class EntryReportStatsServiceImpl implements EntryReportStatsService {

	private static String NACETYLATION_REG_EXP = "^N.*?-acetyl.+$";
    private static String PHOSPHORYLATION_REG_EXP = "^Phospho.*$";

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private EntryPublicationService entryPublicationService;

    @Autowired
    private OverviewService overviewService;

    @Autowired
    private DbXrefService dbXrefService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private AnnotationService annotationService;

    @Cacheable("entry-report-stats")
    @Override
    public EntryReportStats reportEntryStats(String entryAccession) {

        EntryReportStats ers = new EntryReportStats();

        List<DbXref> xrefs = dbXrefService.findDbXrefsByMaster(entryAccession);
        List<Annotation> annotations = annotationService.findAnnotations(entryAccession);

        ers.setAccession(entryAccession);
        setEntryDescription(entryAccession, ers);
        setProteinExistence(entryAccession, ers);
        setIsProteomics(entryAccession, xrefs, annotations, ers);
        setIsAntibody(xrefs, annotations, ers);
        setIs3D(annotations, ers);
        setIsDisease(xrefs, annotations, ers);
        setIsoformCount(entryAccession, ers);
        setVariantCount(entryAccession, ers);
        setPTMCount(annotations, ers);
        setCuratedPublicationCount(entryAccession, ers);
        setAdditionalPublicationCount(entryAccession, ers);
        setPatentCount(entryAccession, ers);
        setSubmissionCount(entryAccession, ers);
        setWebResourceCount(entryAccession, ers);

        return ers;
    }

    @Override
    public boolean isEntryNAcetyled(String entryAccession, Predicate<AnnotationEvidence> isExperimentalPredicate) {

        return containsPtmAnnotation(entryAccession, NACETYLATION_REG_EXP, isExperimentalPredicate);
    }
    
    @Override
    public boolean isEntryPhosphorylated(String entryAccession, Predicate<AnnotationEvidence> isExperimentalPredicate) {

        return containsPtmAnnotation(entryAccession, PHOSPHORYLATION_REG_EXP, isExperimentalPredicate);
    }

    @Override
    public Map<String, String> reportIsoformPeffHeaders(String entryAccession) {

        return isoformService.findIsoformsByEntryName(entryAccession).stream()
                .collect(Collectors.toMap(Isoform::getIsoformAccession,
                        isoform -> IsoformPEFFHeader.toString(isoformService.formatPEFFHeader(isoform.getIsoformAccession()))));
    }

    private void setEntryDescription(String entryAccession, EntryReportStats report) {

        report.setDescription(overviewService.findOverviewByEntry(entryAccession).getRecommendedProteinName().getName());
    }

    private void setIsProteomics(String entryAccession, List<DbXref> xrefs, List<Annotation> annotations, EntryReportStats report) {

    	boolean result = false;
    	
    	if (xrefs.stream().anyMatch(this::isPeptideAtlasOrMassSpecXref)) {
    		result = true;
    	}
    	
    	else if (publicationService.findPublicationsByEntryName(entryAccession).stream()
                .anyMatch(pub -> hasMassSpecScope(entryAccession, pub.getPublicationId()))) {
    		result = true;

    	} else if (annotations.stream()
    			.anyMatch(a -> isPeptideMapping(a) || isNextprotPtmAnnotation(a)  )) {
    		result = true;
    	}

    	report.setPropertyTest(EntryReport.IS_PROTEOMICS, result);
    }

    
    private boolean hasMassSpecScope(String entryAccession, long pubId) {

    	//return pub.getProperty("scope").stream().anyMatch(p -> p.contains("MASS SPECTROMETRY"));
    	return entryPublicationService.findEntryPublications(entryAccession).getEntryPublication(pubId).getDirectLinks(PublicationProperty.SCOPE).stream()
                .anyMatch(p -> p.getLabel().contains("MASS SPECTROMETRY"));
    }
    
    private boolean isPeptideAtlasOrMassSpecXref(DbXref x) {
    	
    	if ("PeptideAtlas".equals(x.getDatabaseName())) return true;
    	
    	if (x.getProperties().stream().anyMatch(
    			p -> p.getName().equals("scope") && p.getValue().contains("MASS SPECTROMETRY"))) return true;
    	
    	return false;
    }
    
    private boolean isPeptideMapping(Annotation a) {
    	return a.getAPICategory()==AnnotationCategory.PEPTIDE_MAPPING;
    }

    private boolean isNextprotPtmAnnotation(Annotation a) {
    	
    	if (a.getAPICategory()!=AnnotationCategory.MODIFIED_RESIDUE && 
			a.getAPICategory()!=AnnotationCategory.GLYCOSYLATION_SITE &&
			a.getAPICategory()!=AnnotationCategory.CROSS_LINK) {
    		return false;
    	}
    	
    	if (a.getEvidences()==null) return false; // should not occur but you know...
    	
    	return a.getEvidences().stream().anyMatch(evi -> "nextprot".equalsIgnoreCase(evi.getAssignedBy()));

    }
    
    private void setIsAntibody(List<DbXref> xrefs, List<Annotation> annotations, EntryReportStats report) {
    	
    	boolean result =false;
    	
    	// this works for public antibodies with accession like HPAxxxx (sequence is known and aligned on isoforms)    	 
		if (annotations.stream()
			.anyMatch(a -> a.getAPICategory()==AnnotationCategory.ANTIBODY_MAPPING)) result=true;
			
		// that works for patented antibodies with accession like CABxxx with an unknow sequence (and thus not aligned / mapped on isoforms)
		if (xrefs.stream()
				.anyMatch(x -> x.getAccession().startsWith("CAB") && x.getDatabaseName().equals("HPA"))) result=true;
		
        report.setPropertyTest(EntryReport.IS_ANTIBODY,result);
    }

    private void setIs3D(List<Annotation> annotations, EntryReportStats report) {

        report.setPropertyTest(EntryReport.IS_3D,
                annotations.stream().
        			anyMatch(a -> a.getAPICategory()==AnnotationCategory.UNIPROT_KEYWORD && "KW-0002".equals(a.getCvTermAccessionCode())));
    }


    /*
     * NP1 specifications: returns true if one of the criteria below is met	
	 * - a disease annotation 
	 * - a uniprot keyword annotation with a disease term 
     * - a xref from orphanet 
     * Note: Orphanet xrefs are turned into annotations, so should never need this criterion
     */
    private void setIsDisease(List<DbXref> xrefs, List<Annotation> annotations, EntryReportStats report) {

        boolean result = false;
        
    	if (annotations.stream().anyMatch(a -> hasDiseaseCategory(a) || hasDiseaseKeywordTerm(a))) {
    		result = true;
    	} else if (xrefs.stream().anyMatch(x -> "Orphanet".equals(x.getDatabaseName()))) {
    		result = true;
    	}    	
        report.setPropertyTest(EntryReport.IS_DISEASE, result);
    }
    

    private boolean hasDiseaseCategory(Annotation a) {
    	return a.getAPICategory()==AnnotationCategory.DISEASE;
    }
    
    private boolean hasDiseaseKeywordTerm(Annotation a) {
    	
    	// existence of a uniprot keyword with category "Disease" except "Proto-oncogene" term name
    	if (a.getAPICategory() != AnnotationCategory.UNIPROT_KEYWORD) return false;
    	if ( ! "Disease".equals(a.getCvTermType())) return false;
    	if ( "Proto-oncogene".equals(a.getCvTermName())) return false;
     	return true;
    }
    
    private void setProteinExistence(String entryAccession, EntryReportStats report) {

        ProteinExistence proteinExistence = overviewService.findOverviewByEntry(entryAccession).getProteinExistences().getProteinExistence();
        if (proteinExistence == null) {
            throw new NextProtException("undefined existence level for neXtProt entry "+ entryAccession);
        }

        report.setProteinExistence(proteinExistence);
    }

    private void setIsoformCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.ISOFORM_COUNT, isoformService.findIsoformsByEntryName(entryAccession).size());
    }

    private void setVariantCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.VARIANT_COUNT, (int) annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.VARIANT)
                .count());
    }

    private void setPTMCount(List<Annotation> annotations, EntryReportStats report) {

        report.setPropertyCount(EntryReport.PTM_COUNT, (int) annotations.stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.SELENOCYSTEINE ||
                                annotation.getAPICategory() == AnnotationCategory.LIPIDATION_SITE ||
                                annotation.getAPICategory() == AnnotationCategory.GLYCOSYLATION_SITE ||
                                annotation.getAPICategory() == AnnotationCategory.CROSS_LINK ||
                                annotation.getAPICategory() == AnnotationCategory.DISULFIDE_BOND ||
                                annotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE
                        // || annotation.getAPICategory() == AnnotationCategory.PTM_INFO
                )
                .count());
    }

    private void setCuratedPublicationCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.CURATED_PUBLICATION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.CURATED));
    }

    private void setAdditionalPublicationCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.ADDITIONAL_PUBLICATION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.ADDITIONAL));
    }

    private void setPatentCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.PATENT_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.PATENT));
    }

    private void setSubmissionCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.SUBMISSION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.SUBMISSION));
    }

    private void setWebResourceCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReport.WEB_RESOURCE_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.WEB_RESOURCE));
    }

    boolean isGoldAnnotation(Annotation annot) {
    	boolean result = annot.getQualityQualifier().equals(QualityQualifier.GOLD.name());
    	//System.out.println("annot " + annot.getAnnotationId() + " quality: " + annot.getQualityQualifier());
    	return result;
    }
    
    boolean annotationTermMatchesPattern(Annotation annot, String ptmRegExp) {
    	boolean result = annot.getCvTermName().matches(ptmRegExp);
    	//System.out.println("annot " + annot.getAnnotationId() + " matches " + ptmRegExp +": " + result);
    	return result;
    }
    
    boolean isGoldEvidence(AnnotationEvidence evi) {
    	boolean result = evi.getQualityQualifier().equals(QualityQualifier.GOLD.name());
    	//System.out.println("annot " + evi.getAnnotationId() +  " evi " + evi.getEvidenceId() + " quality: " + evi.getQualityQualifier());
    	return result;
    }
    
    boolean isExperimentalEvidence(AnnotationEvidence evi, Predicate<AnnotationEvidence> isExperimentalPredicate) {
    	boolean result = isExperimentalPredicate.test(evi);
    	//System.out.println("annot " + evi.getAnnotationId() +  " evi " + evi.getEvidenceId() + " experimental: " + result);
    	return result;
    }

	boolean containsPtmAnnotation(String entryAccession, String ptmRegExp, Predicate<AnnotationEvidence> isExperimentalPredicate) {

        List<Annotation> ptms = annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE)
                .collect(Collectors.toList());

		return nullableListToStream(ptms)
				.anyMatch(annot -> isGoldAnnotation(annot) &&
				 		annotationTermMatchesPattern(annot, ptmRegExp) &&
						annot.getEvidences().stream()
								.anyMatch(evi -> isGoldEvidence(evi) && isExperimentalEvidence(evi,isExperimentalPredicate)
						)
				);
	}

    /**
     * Retrieves publications by master's unique name filtered by a view
     *
     * @param entryAccession the entry accession
     * @param publicationCategory the publication view
     * @return a list of Publication
     */
    private List<EntryPublication> reportPublicationsByEntryName(String entryAccession, PublicationCategory publicationCategory) {

        return entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationList(publicationCategory);
    }

    /**
     * Count the number of publication linked to this entry for the given view
     * @param entryAccession the entry accession
     * @param publicationCategory the publication view
     */
    private int countPublicationsByEntryName(String entryAccession, PublicationCategory publicationCategory) {

        return reportPublicationsByEntryName(entryAccession, publicationCategory).size();
    }
}
