package org.nextprot.api.core.service.statement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.export.StatementExporter;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;

@ActiveProfiles({ "dev" })
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

    //TODO to unignore
    @Ignore
    @Test
    public void exportBrca1() throws FileNotFoundException {

        String content = exporter.exportGeneStatementsAsTsvString("brca1");
        Assert.assertTrue(content.split("\n").length>1);
    }

    @Test
    public void exportUnknownGene() throws FileNotFoundException {

        String content = exporter.exportGeneStatementsAsTsvString("spongebob");
        Assert.assertEquals(1, content.split("\n").length);
    }
}