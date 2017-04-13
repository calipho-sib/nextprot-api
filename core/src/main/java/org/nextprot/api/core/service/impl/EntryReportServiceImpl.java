package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryReportServiceImpl implements EntryReportService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public List<EntryReport> reportEntry(String entryAccession) {

        Entry entry = entryBuilderService.buildWithEverything(entryAccession);

        EntryReport report = new EntryReport();

        report.setAccession(entry.getUniqueName());
        setEntryDescription(entry, report);
        setProteinExistence(entry, report);
        setIsProteomics(entry, report); // TODO: PAM should implement this
        setIsAntibody(entry, report);   // TODO: PAM should implement this
        setIs3D(entry, report);         // TODO: PAM should implement this
        setIsDisease(entry, report);    // TODO: PAM should implement this
        setIsoformCount(entry, report);
        setVariantCount(entry, report);
        setPTMCount(entry, report);

        return mapByChromosomalLocationCollectToList(entry, report);
    }

    private void setEntryDescription(Entry entry, EntryReport report) {

        report.setDescription(entry.getOverview().getRecommendedProteinName().getName());
    }

    private void setIsProteomics(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_PROTEOMICS, false);
    }

    private void setIsAntibody(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_ANTIBODY, false);
    }

    private void setIs3D(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_3D, false);
    }

    private void setIsDisease(Entry entry, EntryReport report) {

        report.setPropertyTest(EntryReport.IS_DISEASE, false);
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

    private List<EntryReport> mapByChromosomalLocationCollectToList(Entry entry, EntryReport report) {

        return entry.getChromosomalLocations().stream()
                .map(report::copyThenSetChromosomalLocation)
                .collect(Collectors.toList());

    }
}
