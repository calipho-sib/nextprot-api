package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.PublicationCategory;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EntryReportStatsServiceImpl implements EntryReportStatsService {

    private static final String DIRECT_LINK_LABEL_MS = "MASS SPECTROMETRY";
    private static final String DIRECT_LINK_LABEL_CHARACTERIZATION_OF_VARIANT = "CHARACTERIZATION OF VARIANT";

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
        List<Publication> publis = publicationService.findPublicationsByEntryName(entryAccession);

        ers.setAccession(entryAccession);
        setEntryDescription(entryAccession, ers);
        setProteinExistence(entryAccession, ers);
        setIsProteomics(entryAccession, annotations, xrefs, publis, ers);
        setIsMutagenesis(entryAccession, annotations, publis, ers);
        setIsAntibody(xrefs, annotations, ers);
        setIs3D(annotations, ers);
        setIsDisease(xrefs, annotations, ers);
        setIsoformCount(entryAccession, ers);
        setVariantCount(annotations, ers);
        setPTMCount(annotations, ers);
        setCuratedPublicationCount(entryAccession, ers);
        setAdditionalPublicationCount(entryAccession, ers);
        setPatentCount(entryAccession, ers);
        setSubmissionCount(entryAccession, ers);
        setWebResourceCount(entryAccession, ers);

        return ers;
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

    private void setIsProteomics(String entryAccession, List<Annotation> annotations, List<DbXref> xrefs, List<Publication> publis, EntryReportStats report) {

    	boolean result = xrefs.stream().anyMatch(this::isPeptideAtlasOrMassSpecXref) ||
                publis.stream().anyMatch(pub -> hasScope(entryAccession, pub.getPublicationId(), DIRECT_LINK_LABEL_MS)) ||
                annotations.stream().anyMatch(a -> isPeptideMapping(a) || isNextprotPtmAnnotation(a));

    	report.setPropertyTest(EntryReportStats.IS_PROTEOMICS, result);
    }

    private void setIsMutagenesis(String entryAccession, List<Annotation> annotations, List<Publication> publis, EntryReportStats report) {

        boolean result = annotations.stream().anyMatch(annotation -> annotation.getAPICategory() == AnnotationCategory.MUTAGENESIS) ||
                publis.stream().anyMatch(pub -> hasScope(entryAccession, pub.getPublicationId(), DIRECT_LINK_LABEL_CHARACTERIZATION_OF_VARIANT));

        report.setPropertyTest(EntryReportStats.IS_MUTAGENESIS, result);
    }

    private boolean hasScope(String entryAccession, long pubId, String expectedDirectLinkLabel) {

        return entryPublicationService.findEntryPublications(entryAccession).getEntryPublication(pubId).getDirectLinks(PublicationProperty.SCOPE).stream()
                .anyMatch(p -> p.getLabel().contains(expectedDirectLinkLabel));
    }
    
    private boolean isPeptideAtlasOrMassSpecXref(DbXref x) {
    	
    	return "PeptideAtlas".equals(x.getDatabaseName()) ||
                x.getProperties().stream().anyMatch(p -> "scope".equals(p.getName()) && "MASS SPECTROMETRY".contains(p.getValue()));
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
				.anyMatch(x -> x.getAccession().startsWith("CAB") && "HPA".equals(x.getDatabaseName()))) result=true;
		
        report.setPropertyTest(EntryReportStats.IS_ANTIBODY,result);
    }

    private void setIs3D(List<Annotation> annotations, EntryReportStats report) {

        report.setPropertyTest(EntryReportStats.IS_3D,
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

        boolean result = annotations.stream().anyMatch(a -> hasDiseaseCategory(a) || hasDiseaseKeywordTerm(a)) ||
            xrefs.stream().anyMatch(x -> "Orphanet".equals(x.getDatabaseName()));

        report.setPropertyTest(EntryReportStats.IS_DISEASE, result);
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

        ProteinExistence proteinExistence = overviewService.findOverviewByEntry(entryAccession).getProteinExistence();
        if (proteinExistence == null) {
            throw new NextProtException("undefined existence level for neXtProt entry "+ entryAccession);
        }

        report.setProteinExistence(proteinExistence);
    }

    private void setIsoformCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.ISOFORM_COUNT, isoformService.findIsoformsByEntryName(entryAccession).size());
    }

    private void setVariantCount(List<Annotation> annotations, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.VARIANT_COUNT, (int) annotations.stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.VARIANT)
                .count());
    }

    private void setPTMCount(List<Annotation> annotations, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.PTM_COUNT, (int) annotations.stream()
                .filter(annotation -> annotation.getAPICategory().isChildOf(AnnotationCategory.GENERIC_PTM) &&
                        annotation.getAPICategory() != AnnotationCategory.PTM_INFO
                )
                .count());
    }

    private void setCuratedPublicationCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.CURATED_PUBLICATION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.CURATED));
    }

    private void setAdditionalPublicationCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.ADDITIONAL_PUBLICATION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.ADDITIONAL));
    }

    private void setPatentCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.PATENT_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.PATENT));
    }

    private void setSubmissionCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.SUBMISSION_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.SUBMISSION));
    }

    private void setWebResourceCount(String entryAccession, EntryReportStats report) {

        report.setPropertyCount(EntryReportStats.WEB_RESOURCE_COUNT,
                countPublicationsByEntryName(entryAccession, PublicationCategory.WEB_RESOURCE));
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
