package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
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

    static final ImmutableList<String> diseaseRelatedKeywords = ImmutableList.of(
            "KW-0014","KW-0015","KW-0020","KW-0023","KW-0026",
            "KW-0036","KW-0038","KW-0043","KW-0065","KW-0069",
            "KW-0070","KW-0083","KW-0087","KW-0122","KW-0144",
            "KW-0161","KW-0172","KW-0182","KW-0192","KW-0199",
            "KW-0209","KW-0213","KW-0214","KW-0218","KW-0219",
            "KW-0225","KW-0241","KW-0242","KW-0248","KW-0250",
            "KW-0263","KW-0307","KW-0316","KW-0322","KW-0331",
            "KW-0335","KW-0355","KW-0360","KW-0361","KW-0362",
            "KW-0363","KW-0367","KW-0370","KW-0380","KW-0429",
            "KW-0431","KW-0435","KW-0451","KW-0454", "KW-0461",
            "KW-0466","KW-0478","KW-0510","KW-0523","KW-0525",
            "KW-0550","KW-0553","KW-0586","KW-0622","KW-0656",
            "KW-0657","KW-0668","KW-0682","KW-0685","KW-0705",
            "KW-0751","KW-0757","KW-0772","KW-0792","KW-0821",
            "KW-0836","KW-0852","KW-0855","KW-0856","KW-0857",
            "KW-0861","KW-0867","KW-0887","KW-0890","KW-0897",
            "KW-0898","KW-0900","KW-0901","KW-0905","KW-0907",
            "KW-0908","KW-0910","KW-0911","KW-0912","KW-0913",
            "KW-0923","KW-0935","KW-0940","KW-0942","KW-0947",
            "KW-0948","KW-0950","KW-0951","KW-0953","KW-0954",
            "KW-0955","KW-0956","KW-0958","KW-0976","KW-0977",
            "KW-0979","KW-0980","KW-0981","KW-0982","KW-0983",
            "KW-0984","KW-0985","KW-0986","KW-0987","KW-0988",
            "KW-0989","KW-0990","KW-0991","KW-0992","KW-0993",
            "KW-1004","KW-1007","KW-1008","KW-1010","KW-1011",
            "KW-1012","KW-1013","KW-1014","KW-1016","KW-1020",
            "KW-1021","KW-1022","KW-1023","KW-1024","KW-1026",
            "KW-1054","KW-1055","KW-1056","KW-1057","KW-1058",
            "KW-1059","KW-1060","KW-1062","KW-1063","KW-1065",
            "KW-1066","KW-1067","KW-1068","KW-1186","KW-1211",
            "KW-1212","KW-1215","KW-1268","KW-1269","KW-1270",
            "KW-1274");

    @Cacheable(value = "entry-report-stats", sync = true)
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
        setIsExpression(annotations, ers);
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

    /*
     * NP1 specifications: returns true if one of the criteria below is met	
	 * - annotations contains an EXPRESSION_PROFILE annotation 
	 * - annotations contains an EXPRESSION_INFO annotation 
     */
    
    private void setIsExpression(List<Annotation> annotations, EntryReportStats report) {
    	boolean hasExpressionAnnotation =  annotations.stream().anyMatch(a -> isExpressionAnnotation(a));
    	report.setPropertyTest(EntryReportStats.IS_EXPRESSION, hasExpressionAnnotation);
    }
    
    private boolean isExpressionAnnotation(Annotation a) {
    	if (a.getAPICategory() == AnnotationCategory.EXPRESSION_PROFILE) return true;
    	if (a.getAPICategory() == AnnotationCategory.EXPRESSION_INFO) return true;
   		return false;
    }
    
    private boolean hasDiseaseKeywordTerm(Annotation a) {
    	
    	// existence of a uniprot keyword with category "Disease" except "Proto-oncogene" term name
    	if (a.getAPICategory() != AnnotationCategory.UNIPROT_KEYWORD) return false;
    	if ( ! "Disease".equals(a.getCvTermType())) return false;
    	if ( "Proto-oncogene".equals(a.getCvTermName())) return false;

    	String cvTermAccession = a.getCvTermAccessionCode();
        if(cvTermAccession != null && !diseaseRelatedKeywords.contains(cvTermAccession)) return false;

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
