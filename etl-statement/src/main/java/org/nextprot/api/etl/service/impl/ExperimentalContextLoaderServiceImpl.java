package org.nextprot.api.etl.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.bio.experimentalcontext.ExperimentalContextStatement;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.etl.service.ExperimentalContextLoaderService;
import org.nextprot.api.etl.service.StatementSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class ExperimentalContextLoaderServiceImpl implements ExperimentalContextLoaderService {

    @Autowired
    StatementSourceService statementSourceService;

    @Autowired
    ExperimentalContextDao experimentalContextDao;

    String EXPERIMENTAL_CONTEXT_CACHE = "experimental-context-dictionary";

    protected static final Logger LOGGER = Logger.getLogger(ExperimentalContextLoaderServiceImpl.class);

    /**
     *  Loads the experimental contexts read from the source files and returns the
     * @param source
     * @param release
     * @param load
     * @return SQL statement for insertion
     */
    @Override
    public String loadExperimentalContexts(StatementSource source, String release, boolean load) {
        // Uses the statement source service to get the input files
        try {
            Set<String> sourceFiles  = statementSourceService.getJsonFilenamesForRelease(source, release);
            String insertStatements = null;
            for(String sourceFile : sourceFiles) {
                // Read the statements at once (unlike in the streaming approach)
                List<ExperimentalContextStatement> experimentalContextStatements = readStatements(source, release, sourceFile);
                LOGGER.info("Read " + experimentalContextStatements.size() + " statements from " + sourceFile);
                try {
                    // Loads the read contexts
                    insertStatements = experimentalContextDao.loadExperimentalContexts(source, experimentalContextStatements, load);
                    LOGGER.info("Loaded " + experimentalContextStatements.size() + " statements ");
                } catch(Exception e) {
                    e.printStackTrace();
                    return e.getLocalizedMessage();
                }
            }
            return insertStatements;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ExperimentalContextStatement> readStatements(StatementSource source, String release, String sourceFile) {
        try {
            String statementJSONArray = statementSourceService.getStatementsAsJsonArray(source, release, sourceFile);
            ObjectMapper mapper = new ObjectMapper();
            List<ExperimentalContextStatement> experimentalContextStatements = mapper.readValue(statementJSONArray, new TypeReference<List<ExperimentalContextStatement>>(){});
            return experimentalContextStatements;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

