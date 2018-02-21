package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.dao.StatementDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.util.Collections;

@Ignore
@ActiveProfiles({ "dev","cache" })
public class AnnotationExporterTest extends CoreUnitBaseTest {

    @Autowired
    private StatementDao statementDao;
    @Autowired
    private EntryBuilderService entryBuilderService;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

    private AnnotationExporter exporter;

    @Before
    public void setup() {

        exporter = new AnnotationExporter(entryBuilderService, statementDao, masterIdentifierService);
    }

    @Test
    public void exportSingleGene() throws FileNotFoundException {

        String tsv = exporter.exportAnnotationStatsAsTsvString(Collections.singletonList("msh6"));
        exporter.exportAsTsvFile("./", "msh6", tsv);

        Assert.assertEquals(34, exporter.getStatisticsMap().size());
    }

    @Test
    public void exportAllGenes() throws FileNotFoundException {

        String tsv = exporter.exportAllGeneStatementsAsTsvString();
        exporter.exportAsTsvFile("./", "all-genes", tsv);

        Assert.assertEquals(52, exporter.getStatisticsMap().size());
    }
}