package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.preprocess.StatementPreProcessService;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementETLPreProcessTest {

    @Autowired
    private StatementPreProcessService statementPreProcessService;

//    @Autowired
//    private StatementLoaderService statementLoaderService;
//
//    @Test
//    public void shouldDoIt() throws SQLException {
//    	
//    	statementLoaderService.deleteRawStatements(StatementSource.ENYO);
//    	statementLoaderService.deleteEntryMappedStatements(StatementSource.ENYO);
//    }
    
    
    @Test
    public void shouldPreprocessBgeeRawStatementAndAddEntryAccession() throws Exception{
        StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
        Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.ENYO, null, "bgee-statements");
        System.out.println("raw: " + rawStatements.size());
        Set<Statement> preprocessedStatements = statementPreProcessService.process(StatementSource.valueOfKey("Bgee"), rawStatements);
        for (Statement s: preprocessedStatements) System.out.println(s);
        Assert.assertEquals(preprocessedStatements.size(),1);

        for (Statement preprocessedStatement : preprocessedStatements) {
            Optional<String> geneName = preprocessedStatement.getOptionalValue("ENSEMBL_ID");
            Assert.assertEquals(geneName.isPresent(), true);

            String entryName = geneName.get();
            if(geneName.equals("ENSG00000000003")) {
                Assert.assertEquals(entryName, "NX_O43657");
            }
        }
    }
}
