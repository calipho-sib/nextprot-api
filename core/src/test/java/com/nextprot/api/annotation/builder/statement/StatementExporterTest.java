package com.nextprot.api.annotation.builder.statement;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
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