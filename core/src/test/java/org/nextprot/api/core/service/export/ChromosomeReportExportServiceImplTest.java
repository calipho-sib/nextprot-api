package org.nextprot.api.core.service.export;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.nextprot.api.core.service.export.ChromosomeReportExportServiceImpl.NACETYLATION_REG_EXP;
import static org.nextprot.api.core.service.export.ChromosomeReportExportServiceImpl.PHOSPHORYLATION_REG_EXP;

@ActiveProfiles({ "dev" })
public class ChromosomeReportExportServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private AnnotationService annotationService;

    @Test
    public void containsPtmTerm() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_A0A096LP55").withAnnotations());
        //N6-acetyllysine
        Assert.assertTrue(ChromosomeReportExportServiceImpl.containsPtmAnnotation(entry, NACETYLATION_REG_EXP, annotationService));

        entry = entryBuilderService.build(EntryConfig.newConfig("NX_P63165").withAnnotations());
        //N-acetylserine
        Assert.assertTrue(ChromosomeReportExportServiceImpl.containsPtmAnnotation(entry, PHOSPHORYLATION_REG_EXP, annotationService));
    }
}