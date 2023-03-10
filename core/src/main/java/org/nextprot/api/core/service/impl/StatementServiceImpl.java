package org.nextprot.api.core.service.impl;

import static org.nextprot.commons.statements.specs.CoreStatementField.REFERENCE_ACCESSION;
import static org.nextprot.commons.statements.specs.CoreStatementField.REFERENCE_DATABASE;
import static org.nextprot.commons.statements.specs.CoreStatementField.RESOURCE_TYPE;
import static org.nextprot.commons.statements.specs.CoreStatementField.SUBJECT_ANNOTATION_IDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.CvDatabase;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.SimpleService;
import org.nextprot.api.core.service.StatementEntryAnnotationBuilder;
import org.nextprot.api.core.service.StatementService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverDelegate;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StatementServiceImpl implements StatementService {

    @Autowired private StatementDao statementDao;
    @Autowired private TerminologyService terminologyService;
    @Autowired private PublicationService publicationService;
    @Autowired private MainNamesService mainNamesService;
    @Autowired private DbXrefService dbXrefService;
    @Autowired private ExperimentalContextService experimentalContextService;
    @Autowired private SimpleService simpleService;


    private List<Annotation> getProteoformEntryAnnotations(String entryAccession) {

        List<Statement> proteoformStatements = statementDao.findProteoformStatements(entryAccession);

        //Collect all subjects
        List<String> subjectAnnotIds = proteoformStatements.stream().map(s ->
                Arrays.asList(s.getValue(SUBJECT_ANNOTATION_IDS).split(","))
        ).flatMap(Collection::stream).collect(Collectors.toList());

        List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(subjectAnnotIds);

        return StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService, experimentalContextService).buildProteoformIsoformAnnotations(entryAccession, subjects, proteoformStatements);

    }


    private List<Annotation> getNormalEntryAnnotations(String entryAccession) {
        List<Statement> normalStatements = statementDao.findNormalStatements(entryAccession);
        return StatementEntryAnnotationBuilder
        		.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService, experimentalContextService)
        		.buildAnnotationList(entryAccession, normalStatements);
    }


    @Cacheable(value = "statement-entry-annotations", sync = true)
    @Override
    public List<Annotation> getAnnotations(String entryAccession) {

        List<Annotation> list = getProteoformEntryAnnotations(entryAccession);
        List<Annotation> normalEntryAnnotations = getNormalEntryAnnotations(entryAccession);
        matchRelatedAnnotations(normalEntryAnnotations);
        list.addAll(normalEntryAnnotations);

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
    	
    	if (! "database".equals(statement.getValue(RESOURCE_TYPE))) return null; // skip publications
    	String db = statement.getValue(REFERENCE_DATABASE);
    	String expectedDbs = "Bgee,IntAct,GlyConnect";
    	if (! expectedDbs.contains(db)) {
    		System.out.println("WARNING: trying to build xref from statement with unexpected REFERENCE_DATABASE: " + statement.toString());
    	}
    	return newStatementXref(statement);    	
    }


    private DbXref newStatementXref(Statement statement) {

        String referenceDB = statement.getValue(REFERENCE_DATABASE);
        String referenceAC = statement.getValue(REFERENCE_ACCESSION);
        DbXref xref = new DbXref();
        xref.setAccession(referenceAC);
        xref.setDatabaseName(referenceDB);
        xref.setDbXrefId(dbXrefService.findXrefId(referenceDB, referenceAC));
        DbXrefURLResolverDelegate resolver = new DbXrefURLResolverDelegate(); // IMPORTANT: helps to fill xref linkUrl and resolvedUrl fields
        resolver.resolve(xref); 
        CvDatabase db = simpleService.getNameDatabaseMap().get(referenceDB);  // IMPORTANT: helps to fill some other less crucial xref fields
        xref.setDatabaseCategory(db.getCatName());    
        xref.setUrl(db.getUrl());
        //System.out.println("new xref with ac: " + referenceAC + " db:" + referenceDB + " id:" + xref.getDbXrefId() + " ru:" + xref.getResolvedUrl() +  " url:" + xref.getUrl());
        xref.setProperties(new ArrayList<>());
        return xref;	
    }


    /**
     * Matches the interaction mapping and binary interaction annotations
     * @param annotations
     */
    private void matchRelatedAnnotations(List<Annotation> annotations) {
        List<Annotation> binaryInteractions = annotations.stream()
                .filter(annotation -> AnnotationCategory.BINARY_INTERACTION.equals(annotation.getAPICategory()))
                .collect(Collectors.toList());

        List<Annotation> interactionMappings = annotations.stream()
                .filter(annotation -> AnnotationCategory.INTERACTION_MAPPING.equals(annotation.getAPICategory()))
                .collect(Collectors.toList());

        for(Annotation interactionMapping: interactionMappings) {
            for(Annotation binaryInteraction: binaryInteractions) {
                BioObject interactantFromBinaryInteraction = binaryInteraction.getBioObject();
                BioObject interactantFromInteractionMapping = interactionMapping.getBioObject();
                if(interactantFromBinaryInteraction.getAccession().equals(interactantFromInteractionMapping.getAccession())) {
                    binaryInteraction.addRelatedAnnotationName(interactionMapping.getUniqueName());
                    interactionMapping.addRelatedAnnotationName(binaryInteraction.getUniqueName());
                }
            }
        }
    }
}
