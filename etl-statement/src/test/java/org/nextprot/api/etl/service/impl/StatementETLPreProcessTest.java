package org.nextprot.api.etl.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.preprocess.StatementPreProcessService;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementETLPreProcessTest {

    @Autowired
    private StatementPreProcessService statementPreProcessService;

    @Test
    public void shouldPreprocessBgeeRawStatementAndAddEntryAccession() throws Exception{
        StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
        Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.ENYO, null, "enyo-statements");
        statementPreProcessService.process(StatementSource.valueOfKey("Bgee"), rawStatements);
    }
}
