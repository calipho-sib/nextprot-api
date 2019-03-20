package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.StatementEntryAnnotationBuilder;
import org.nextprot.api.core.service.StatementService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatementServiceImpl implements StatementService {

    @Autowired
    public StatementDao statementDao;

    @Autowired
    public TerminologyService terminologyService;

    @Autowired
    public PublicationService publicationService;

    @Autowired
    public MainNamesService mainNamesService;

    @Autowired
    public DbXrefService dbXrefService;


    private List<Annotation> getProteoformEntryAnnotations(String entryAccession) {

        List<Statement> proteoformStatements = statementDao.findProteoformStatements(entryAccession);

        //Collect all subjects
        List<String> subjectAnnotIds = proteoformStatements.stream().map(s ->
                Arrays.asList(s.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(","))
        ).flatMap(Collection::stream).collect(Collectors.toList());

        List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(subjectAnnotIds);

        return StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService).buildProteoformIsoformAnnotations(entryAccession, subjects, proteoformStatements);

    }


    private List<Annotation> getNormalEntryAnnotations(String entryAccession) {
        List<Statement> normalStatements = statementDao.findNormalStatements(entryAccession);
        return StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService).buildAnnotationList(entryAccession, normalStatements);
    }


    @Cacheable(value = "statement-entry-annotations", sync = true)
    @Override
    public List<Annotation> getAnnotations(String entryAccession) {

        List<Annotation> list = getProteoformEntryAnnotations(entryAccession);
        list.addAll(getNormalEntryAnnotations(entryAccession));

        return list;
    }

    @Override
    public Set<DbXref> findDbXrefs(String entryAccession) {

        return statementDao.findNormalStatements(entryAccession).stream()
                .map(statement -> Optional.ofNullable(createDbXref(statement)))
                .flatMap(xref -> xref.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toSet());
    }

    private DbXref createDbXref(Statement statement) {

        if (statement.getValue(StatementField.REFERENCE_DATABASE).equals(XrefDatabase.GLY_CONNECT.getName())) {

            return newGlyConnectXref(statement);
        }

        return null;
    }

    private DbXref newGlyConnectXref(Statement statement) {

        String referenceDB = XrefDatabase.GLY_CONNECT.getName();
        String referenceAC = statement.getValue(StatementField.REFERENCE_ACCESSION);

        try {
            DbXref dbXRef = new DbXref();

            dbXRef.setDbXrefId(dbXrefService.findXrefId(referenceDB, referenceAC));
            dbXRef.setAccession(referenceAC);
            dbXRef.setDatabaseCategory("Sequence databases");
            dbXRef.setDatabaseName(referenceDB);
            dbXRef.setUrl("https://glyconnect.expasy.org");
            dbXRef.setLinkUrl(CvDatabasePreferredLink.GLY_CONNECT.getLink());
            dbXRef.setProperties(new ArrayList<>());

            return dbXRef;
        } catch (DbXrefServiceImpl.MissingCvDatabaseException e) {

            throw new NextProtException("Cannot create dbxref for GlyConnect statement " + statement.getStatementId() + ": " + e.getMessage());
        }
    }
}
