package org.nextprot.api.etl.statement.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.etl.statement.service.StatementETLService;
import org.nextprot.api.etl.statement.service.StatementRemoteService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.nextprot.commons.statements.service.StatementLoaderService;
import org.nextprot.commons.statements.service.impl.OracleStatementLoaderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;

@Service
public class StatementETLServiceImpl implements StatementETLService {

	@Autowired
	private StatementRemoteService statementRemoteService;

	@Autowired
	private IsoformMappingService isoformMappingService;

	private StatementLoaderService statementLoadService = new OracleStatementLoaderServiceImpl();

	@Override
	public String etlStatements(String source) {

		List<Statement> sourceStatements = statementRemoteService.getStatementsForSource(NextProtSource.BioEditor);
		//List<Statement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName(NextProtSource.BioEditor, "msh2");
		//List<Statement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName(NextProtSource.BioEditor, "scn9a");

		//System.err.println("Got response from source");
		Map<String, Statement> sourceStatementsById = sourceStatements.stream().collect(Collectors.toMap(Statement::getStatementId, Function.identity()));


		Set<Statement> statementsMappedToIsoformToLoad = new HashSet<Statement>();
		Set<Statement> statementsMappedToEntryToLoad = new HashSet<Statement>();

		for (Statement originalStatement : sourceStatements) {

			String annotCat = originalStatement.getValue(StatementField.ANNOTATION_CATEGORY);

			if ("functional-impact".equals(annotCat) || "phenotype".equals(annotCat)) {

				String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();
				Set<Statement> subjectStatements = getSubjects(subjectStatemendIds, sourceStatementsById);

				String entryAccession = subjectStatements.iterator().next().getValue(StatementField.ENTRY_ACCESSION);

				boolean isIsoSpecific = false;
				if (isSubjectIsoSpecific(subjectStatements)) {
					String isoformName = checkThatSubjectsAreOnSameIsoform(subjectStatements);
					if(isoformName != null){
						isIsoSpecific = true;
					}else throw new NextProtException("Something wrong occured when checking for iso specificity");
				}
				
					statementsMappedToIsoformToLoad.addAll(mapComplexStatements(AnnotationType.ISOFORM, originalStatement, sourceStatementsById, subjectStatements, entryAccession, !isIsoSpecific));
					statementsMappedToEntryToLoad.addAll(mapComplexStatements(AnnotationType.ENTRY, originalStatement, sourceStatementsById, subjectStatements, entryAccession, !isIsoSpecific));
					
				}

		}

		System.err.println("Deleting...");
		statementLoadService.deleteStatementsForSource(NextProtSource.BioEditor);

		System.err.println("Loading raw statements: " + sourceStatements.size());
		statementLoadService.loadRawStatementsForSource(new HashSet<>(sourceStatements), NextProtSource.BioEditor);

		System.err.println("Loading iso statements: " + statementsMappedToIsoformToLoad.size());
		statementLoadService.loadStatementsMappedToIsoSpecAnnotationsForSource(statementsMappedToIsoformToLoad, NextProtSource.BioEditor);

		System.err.println("Loading entry statements: " + statementsMappedToEntryToLoad.size());
		statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(statementsMappedToEntryToLoad, NextProtSource.BioEditor);

		System.err.println("Finished to load" + statementsMappedToIsoformToLoad.size());

		
		return "yo";

	}
	
	private Set<Statement> mapComplexStatements(AnnotationType type, Statement originalStatement, Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean propagate){
		
		Set<Statement> statementsToLoad = new HashSet<Statement>();

		Map<String, List<Statement>> variantsOnIsoform = getPropagatedStatements(subjectStatements, nextprotAcession, propagate, type);
		
		variantsOnIsoform.keySet().stream().forEach(isoform -> {
				
				List<Statement> subjects = variantsOnIsoform.get(isoform);

				//Load objects
				Statement phenotypeIsoStatement =  null;
				Statement objectIsoStatement = null;

				Statement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());
				if(objectStatement != null){
					objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).build();
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).addSubjects(subjects).addObject(objectIsoStatement).build();
				}else {
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).addSubjects(subjects).build();

				}


				//Load subjects
				subjects.forEach(s -> statementsToLoad.add(s));
				
				//Load VPs
				statementsToLoad.add(phenotypeIsoStatement);
				
				//Load objects
				if(objectIsoStatement != null){
					statementsToLoad.add(objectIsoStatement);
				}


		});
		
		return statementsToLoad;

		
	}

	
	
	/**
	 * Returns an exception if there are mixes between subjects
	 * 
	 * @param subjects
	 * @return
	 */
	private static String checkThatSubjectsAreOnSameIsoform(Set<Statement> subjects) {

		Set<String> isoforms = subjects.stream().map(s -> {
			return s.getValue(StatementField.NEXTPROT_ACCESSION) + "-" + SequenceVariantUtils.getIsoformName(s.getValue(StatementField.ANNOTATION_NAME));
		}).collect(Collectors.toSet());

		if (isoforms.size() != 1) {
			throw new NextProtException("Mixing iso numbers for subjects is now allowed");
		}
		String isoform = isoforms.iterator().next();
		if (isoform == null) {
			throw new NextProtException("Not iso specific subjects are not allowed on isOnSameIsoform");
		}

		return isoform;
	}

	/**
	 * Returns an exception if there are mixes between subjects
	 * 
	 * @param subjects
	 * @return
	 */
	private static boolean isSubjectIsoSpecific(Set<Statement> subjects) {
		int isoSpecificSize = subjects.stream().filter(s -> SequenceVariantUtils.isIsoSpecific(s.getValue(StatementField.ANNOTATION_NAME))).collect(Collectors.toList()).size();
		if (isoSpecificSize == 0) {
			return false;
		} else if (isoSpecificSize == subjects.size()) {
			return true;
		} else {
			throw new NextProtException("Mixing iso specific subjects with non-iso specific variants is not allowed");
		}
	}

	private static Set<Statement> getSubjects(String[] subjectIds, Map<String, Statement> sourceStatementsById) {
		Set<Statement> variants = new HashSet<>();
		for (String subjectId : subjectIds) {
			Statement subjectStatement = sourceStatementsById.get(subjectId);
			if (subjectStatement == null) {
				throw new NextProtException("Subject " + subjectId + " not present in the given list");
			}
			variants.add(subjectStatement);
		}
		return variants;
	}

	public Set<Statement> findSourceStatementsWhereOriginalStatementIsUsedAsSubject(Statement originalStatement, Set<Statement> sourceStatementsWithAModifiedSubject) {
		return sourceStatementsWithAModifiedSubject.stream().filter(sm -> sm.getSubjectStatementIds().contains(originalStatement.getStatementId())).collect(Collectors.toSet());
	}

	private Map<String, List<Statement>> getPropagatedStatements(Set<Statement> multipleSubjects, String nextprotAccession, boolean propagate, AnnotationType type) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult = null;
			if(propagate){
				featureQueryResult = isoformMappingService.propagateFeature(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession);
			}else {
				featureQueryResult = isoformMappingService.validateFeature(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession);
			}

			if (featureQueryResult.isSuccess()) {
				
				if(type.equals(AnnotationType.ISOFORM)){
					result.addAll(mapStatementsToEachIsoform(subject, (FeatureQuerySuccess) featureQueryResult));
				}else {
					result.add(mapStatementToEntry(subject, (FeatureQuerySuccess) featureQueryResult));
				}

			} else {
				FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
				System.err.println("Failure for " + subject.getStatementId() + " " + failure.getError().getMessage());
			}
		}

		// Group the subjects by isoform
		Map<String, List<Statement>> subjectsByIsoform = result.stream().collect(Collectors.groupingBy(s -> (String) s.getValue(StatementField.ISOFORM_ACCESSION)));

		// Filter only subjects that contain all original subjects (the size is
		// the same). In other words, if 2 multiples mutants can not be mapped
		// to all isoform, the statement is not valid
		return subjectsByIsoform.entrySet().stream().filter(map -> map.getValue().size() == multipleSubjects.size()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

	}

	private List<Statement> mapStatementsToEachIsoform(Statement statement, FeatureQuerySuccess result) {

		List<Statement> statementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			if (isoformFeatureResult.isMapped()) {

				Statement rs = StatementBuilder.createNew().addMap(statement).addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformAccession())
						.addField(StatementField.RAW_STATEMENT_ID, statement.getStatementId()) // Keep  a reference to the original statement
						.addField(StatementField.LOCATION_BEGIN, String.valueOf(isoformFeatureResult.getBeginIsoformPosition()))
						.addField(StatementField.LOCATION_END, String.valueOf(isoformFeatureResult.getEndIsoformPosition()))
						.addField(StatementField.LOCATION_BEGIN_MASTER, String.valueOf(isoformFeatureResult.getBeginMasterPosition()))
						.addField(StatementField.LOCATION_END_MASTER, String.valueOf(isoformFeatureResult.getEndMasterPosition()))
						.addField(StatementField.ISOFORM_CANONICAL, String.valueOf(isoformFeatureResult.isCanonical()))
						.addField(StatementField.ANNOTATION_NAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature())).build();

				statementList.add(rs);

			}

		}

		return statementList;
	}
	
	
	private Statement mapStatementToEntry(Statement statement, FeatureQuerySuccess result) {

		Map<String, Pair<Integer, Integer>> isoformAndPositions = new TreeMap<>();

		String beginPositionOfCanonicalOrIsoSpec = null;
		String endPositionOfCanonicalOrIsoSpec = null;
		
		String masterBeginPosition = null;
		String masterEndPosition = null;
		
		String isoCanonical = null;
		
		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {
			if (isoformFeatureResult.isMapped()) {
				isoformAndPositions.put(isoformFeatureResult.getIsoformAccession(), new Pair(isoformFeatureResult.getBeginIsoformPosition(), isoformFeatureResult.getEndIsoformPosition()));
				
				//Will be set in case that we don't want to propagate to canonical
				if(beginPositionOfCanonicalOrIsoSpec == null){
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
				}
				if(endPositionOfCanonicalOrIsoSpec == null){
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}
				
				//If possible use canonical
				if (isoformFeatureResult.isCanonical()) {
					if(isoCanonical != null){
						throw new NextProtException("Canonical position set already" );
					}
					isoCanonical = isoformFeatureResult.getIsoformAccession();
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}

				if(masterBeginPosition == null){
					masterBeginPosition = String.valueOf(isoformFeatureResult.getBeginMasterPosition());
				}
				
				if(masterEndPosition == null){
					masterEndPosition = String.valueOf(isoformFeatureResult.getEndMasterPosition());
				}
				
				if(masterBeginPosition != null){
					if(!masterBeginPosition.equals(String.valueOf(isoformFeatureResult.getBeginMasterPosition()))){
						throw new NextProtException("Begin master position " + masterBeginPosition + " does not match " + String.valueOf(isoformFeatureResult.getBeginMasterPosition() + " for different isoforms (" + result.getData().values().size() + ") for statement " + statement.getStatementId()));
					}
				}
				
				if(masterEndPosition != null){
					if(!masterEndPosition.equals(String.valueOf(isoformFeatureResult.getEndMasterPosition()))){
						throw new NextProtException("End master position does not match for different isoforms"  + statement.getStatementId());
					}
				}



			}

		}
		
		String targetIsoforms = "";
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			targetIsoforms = mapper.writeValueAsString(isoformAndPositions);
		} catch (JsonProcessingException e) {
			throw new NextProtException("Failed to convert" + e.getLocalizedMessage());
		}
		
		Statement rs = StatementBuilder.createNew().addMap(statement)
				.addField(StatementField.RAW_STATEMENT_ID, statement.getStatementId()) // Keep  a reference to the original statement
				.addField(StatementField.LOCATION_BEGIN, beginPositionOfCanonicalOrIsoSpec)
				.addField(StatementField.LOCATION_END, endPositionOfCanonicalOrIsoSpec)
				.addField(StatementField.LOCATION_BEGIN_MASTER, masterBeginPosition)
				.addField(StatementField.LOCATION_END_MASTER, masterEndPosition)
				.addField(StatementField.ISOFORM_CANONICAL, isoCanonical)
				.addField(StatementField.TARGET_ISOFORMS, targetIsoforms)
				.build();

		return rs;
	}


}
