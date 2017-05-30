package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Service
public class EntryReportServiceImpl implements EntryReportService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public List<EntryReport> reportEntry(String entryAccession) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession)
                .withOverview()
                .withAnnotations()
                .withChromosomalLocations()
        );

        EntryReport report = new EntryReport();

        report.setAccession(entry.getUniqueName());
        setEntryDescription(entry, report);
        setProteinExistence(entry, report);
        setIsProteomics(entry, report); // DONE: PAM should implement this
        setIsAntibody(entry, report);   // DONE: PAM should implement this
        setIs3D(entry, report);         // DONE: PAM should implement this
        setIsDisease(entry, report);    // DONE: PAM should implement this
        setIsoformCount(entry, report);
        setVariantCount(entry, report);
        setPTMCount(entry, report);

        return duplicateReportForEachGene(entry, report);
    }

    private void setEntryDescription(Entry entry, EntryReport report) {

        report.setDescription(entry.getOverview().getRecommendedProteinName().getName());
    }

    private void setIsProteomics(Entry entry, EntryReport report) {

    	boolean result = false;
    	
    	if (entry.getXrefs().stream()
    			.anyMatch(x -> isPeptideAtlasOrMassSpecXref(x))) {
    		result = true;
    	
    	} else if (entry.getAnnotations().stream()
    			.anyMatch(a -> isPeptideOrPtmAnnotation(a))) {
    		result = true;
    	}

    	report.setPropertyTest(EntryReport.IS_PROTEOMICS, result);
    }

    private boolean isPeptideAtlasOrMassSpecXref(DbXref x) {
    	
    	if ("PeptideAtlas".equals(x.getDatabaseName())) return true;
    	
    	if (x.getProperties().stream().anyMatch(
    			p -> p.getName().equals("scope") && p.getValue().contains("MASS SPECTROMETRY"))) return true;
    	
    	return false;
    }
    
    private boolean isPeptideOrPtmAnnotation(Annotation a) {
    	if (a.getAPICategory()==AnnotationCategory.PEPTIDE_MAPPING) return true;
    	if (a.getAPICategory()==AnnotationCategory.MODIFIED_RESIDUE) return true;
    	if (a.getAPICategory()==AnnotationCategory.GLYCOSYLATION_SITE) return true;
    	if (a.getAPICategory()==AnnotationCategory.CROSS_LINK) return true;
    	return false;
    }
    	
    private void setIsAntibody(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_ANTIBODY, 
        		entry.getAnnotations().stream()
        			.anyMatch(a -> a.getAPICategory()==AnnotationCategory.ANTIBODY_MAPPING));
    }

    private void setIs3D(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_3D, 
        		entry.getAnnotations().stream().
        			anyMatch(a -> a.getAPICategory()==AnnotationCategory.UNIPROT_KEYWORD && "KW-0002".equals(a.getCvTermAccessionCode())));
    }


    /*
     * NP1 specifications: returns true if one of the criteria below is met	
	 * - a disease annotation 
	 * - a uniprot keyword annotation with a disease term 
     * - a xref from orphanet 
     * Note: Orphanet xrefs are turned into annotations, so should never need this criterion
     */
    private void setIsDisease(Entry entry, EntryReport report) {

        boolean result = false;
        
    	if (entry.getAnnotations().stream().anyMatch(a -> hasDiseaseCategory(a) || hasDiseaseKeywordTerm(a))) {
    		result = true;
    	} else if (entry.getXrefs().stream().anyMatch(x -> "Orphanet".equals(x.getDatabaseName()))) {
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
    
    private void setProteinExistence(Entry entry, EntryReport report) {

        Integer proteinExistenceLevel = entry.getProteinExistenceLevel();
        if (proteinExistenceLevel == null) {
            throw new NextProtException("undefined existence level for neXtProt entry "+ entry.getUniqueName());
        }

        report.setProteinExistence(ProteinExistenceLevel.valueOfLevel(proteinExistenceLevel));
    }

    private void setIsoformCount(Entry entry, EntryReport report) {

        report.setPropertyCount(EntryReport.ISOFORM_COUNT, entry.getIsoforms().size());
    }

    private void setVariantCount(Entry entry, EntryReport report) {

        report.setPropertyCount(EntryReport.VARIANT_COUNT, (int) entry.getAnnotations().stream()
                .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.VARIANT)
                .count());
    }

    private void setPTMCount(Entry entry, EntryReport report) {

        report.setPropertyCount(EntryReport.PTM_COUNT, (int) entry.getAnnotations().stream()
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

    private List<EntryReport> duplicateReportForEachGene(Entry entry, EntryReport report) {

        List<ChromosomalLocation> chromosomalLocations = entry.getChromosomalLocations();

        if (chromosomalLocations.isEmpty()) {
            throw new NextProtException("Cannot make report for entry "  + report.getAccession() + ": no chromosome location found");
        }
        else if (chromosomalLocations.size() == 1) {
            report.setChromosomalLocation(chromosomalLocations.get(0));
            return Collections.singletonList(report);
        }

        return duplicateReportForEachGene(chromosomalLocations, report);
    }

    private List<EntryReport> duplicateReportForEachGene(List<ChromosomalLocation> chromosomalLocations, EntryReport report) {

        return chromosomalLocations.stream()
        		.filter(ChromosomalLocation::isGoldMapping)
                .collect(
                        Collectors.groupingBy(cl -> cl.getRecommendedName()+cl.getStrand(), // group by gene name/strand
                        Collectors.reducing(new BestChromosomeLocationOperator()))          // keep the best location for this gene
                )
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(report::duplicateThenSetChromosomalLocation)
                .collect(Collectors.toList());
    }

    private static class BestChromosomeLocationOperator implements BinaryOperator<ChromosomalLocation> {

        @Override
        public ChromosomalLocation apply(ChromosomalLocation chromosomalLocation, ChromosomalLocation chromosomalLocation2) {

            if (chromosomalLocation.isBestGeneLocation())
                return chromosomalLocation;

            return chromosomalLocation2;
        }
    }
}
