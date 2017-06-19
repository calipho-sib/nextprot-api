package org.nextprot.api.core.service.export;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ChromosomeReportExportService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.Predicate;

@ActiveProfiles({ "dev" })
public class ChromosomeReportExportServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private ChromosomeReportExportService chromosomeReportExportService;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private AnnotationService annotationService;

    @Test
    public void containsPtmTerm() throws Exception {

        Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_A0A096LP55").withAnnotations());
        //N6-acetyllysine
        //Assert.assertTrue(chromosomeReportExportService.containsPtmAnnotation(entry, NACETYLATION_REG_EXP, isExperimentalPredicate));

        entry = entryBuilderService.build(EntryConfig.newConfig("NX_P63165").withAnnotations());
        //N-acetylserine
        //eeAssert.assertTrue(chromosomeReportExportService.containsPtmAnnotation(entry, PHOSPHORYLATION_REG_EXP, isExperimentalPredicate));
    }
}