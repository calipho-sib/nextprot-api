package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryGeneReportService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.GeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryGeneReportServiceImpl implements EntryGeneReportService {

    @Autowired
    private EntryReportStatsService entryReportStatsService;

    @Autowired
    private GeneService geneService;

    @Cacheable("entry-reports")
    @Override
    public List<EntryReport> reportEntry(String entryAccession) {

        EntryReportStats ers = entryReportStatsService.reportEntryStats(entryAccession);

        EntryReport report = new EntryReport();

        report.setAccession(entryAccession);
        report.setDescription(ers.getDescription());
        report.setProteinExistence(ers.getProteinExistence());
        report.setPropertyTest(EntryReport.IS_PROTEOMICS, ers.isProteomics());
        report.setPropertyTest(EntryReport.IS_ANTIBODY, ers.isAntibody());
        report.setPropertyTest(EntryReport.IS_3D, ers.is3D());
        report.setPropertyTest(EntryReport.IS_DISEASE, ers.isDisease());
        report.setPropertyCount(EntryReport.ISOFORM_COUNT, ers.countIsoforms());
        report.setPropertyCount(EntryReport.VARIANT_COUNT, ers.countVariants());
        report.setPropertyCount(EntryReport.PTM_COUNT, ers.countPTMs());
        report.setPropertyCount(EntryReport.CURATED_PUBLICATION_COUNT, ers.countCuratedPublications());
        report.setPropertyCount(EntryReport.ADDITIONAL_PUBLICATION_COUNT, ers.countAdditionalPublications());
        report.setPropertyCount(EntryReport.PATENT_COUNT, ers.countPatents());
        report.setPropertyCount(EntryReport.SUBMISSION_COUNT, ers.countSubmissions());
        report.setPropertyCount(EntryReport.WEB_RESOURCE_COUNT, ers.countWebResources());

        return duplicateReportForEachGene(entryAccession, report);
    }

    private List<EntryReport> duplicateReportForEachGene(String entryAccession, EntryReport report) {

        List<ChromosomalLocation> chromosomalLocations = geneService.findChromosomalLocationsByEntry(entryAccession);

        if (chromosomalLocations.isEmpty()) {
            throw new NextProtException("Cannot make report for entry "  + report.getAccession() + ": no chromosome location found");
        }
        else if (chromosomalLocations.size() == 1) {
            report.setChromosomalLocation(chromosomalLocations.get(0));
            return Collections.singletonList(report);
        }

        return chromosomalLocations.stream()
                .filter(ChromosomalLocation::isGoldMapping)
                .map(report::duplicateThenSetChromosomalLocation)
                .collect(Collectors.toList());
    }
}
