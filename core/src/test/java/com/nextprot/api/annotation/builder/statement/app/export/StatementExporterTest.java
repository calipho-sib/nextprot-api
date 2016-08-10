package com.nextprot.api.annotation.builder.statement.app.export;

import com.google.common.collect.Sets;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;

@ActiveProfiles({ "dev", "cache" })
public class StatementExporterTest extends CoreUnitBaseTest {

    @Autowired
    private StatementDao statementDao;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

    private StatementExporter exporter;

    @Before
    public void setup() {

        exporter = new StatementExporter(statementDao, masterIdentifierService);
    }

    @Test
    public void exportBrca1() throws FileNotFoundException {

        exporter.fetchGeneStatements("brca1");

        Assert.assertEquals(1, exporter.getFetchedAccessions().size());
    }

    @Test
    public void exportUnknownGene() throws FileNotFoundException {

        exporter.fetchGeneStatements("spongebob");

        Assert.assertTrue(exporter.getFetchedAccessions().isEmpty());
    }

    @Test
    public void exportBrca1Twice() throws FileNotFoundException {

        exporter.fetchGeneStatements("brca1");
        int len = exporter.exportAsTsvString().length();
        exporter.fetchGeneStatements("brca1");

        Assert.assertEquals(len, exporter.exportAsTsvString().length());
        Assert.assertEquals(1, exporter.getFetchedAccessions().size());
    }

    @Test
    public void export2genes() throws FileNotFoundException {

        exporter.fetchGeneSetStatements(Sets.newHashSet("brca1", "scn9A"));

        Assert.assertEquals(2, exporter.getFetchedAccessions().size());
    }

    @Test
    public void exportAllGenes() throws FileNotFoundException {

        exporter.fetchAllGeneStatements();

        Assert.assertEquals(20, exporter.getFetchedAccessions().size());
    }
}