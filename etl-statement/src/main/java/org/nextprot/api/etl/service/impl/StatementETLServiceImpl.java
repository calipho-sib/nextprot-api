package org.nextprot.api.etl.service.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

@Service
public class StatementETLServiceImpl implements StatementETLService {
	
	private static final Logger LOGGER = Logger.getLogger(StatementETLServiceImpl.class);

	@Autowired private IsoformService isoformService;
	
	@Autowired	private StatementExtractorService statementRemoteService;

	@Autowired	private IsoformMappingService isoformMappingService;

	@Autowired	private StatementLoaderService statementLoadService = null;

	Set<Statement> extractStatements(NextProtSource source) {
		Set<Statement> statements =  statementRemoteService.getStatementsForSource(source);
		LOGGER.info("Extracting " + statements.size() + " raw statements from " + source.name() + " in " + source.getStatementsUrl());
		return statements;
	}


	Set<Statement> transformStatements(Set<Statement> rawStatements) {

		Map<String, Statement> sourceStatementsById = rawStatements.stream().collect(Collectors.toMap(Statement::getStatementId, Function.identity()));

		Set<Statement> statementsMappedToEntryToLoad = new HashSet<>();

		for (Statement originalStatement : rawStatements) {

			if ((originalStatement.getSubjectStatementIds() != null) && (!originalStatement.getSubjectStatementIds().isEmpty())) {

				String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();
				Set<Statement> subjectStatements = getSubjects(subjectStatemendIds, sourceStatementsById);

				String entryAccession = subjectStatements.iterator().next().getValue(StatementField.ENTRY_ACCESSION);

				boolean isIsoSpecific = false;
				String isoformName = validateSubject(subjectStatements);
				String isoformSpecificAccession = null;

				if (isSubjectIsoSpecific(subjectStatements)) {
					if(isoformName != null){
						isIsoSpecific = true;
						String featureName = subjectStatements.iterator().next().getValue(StatementField.ANNOTATION_NAME);
						isoformSpecificAccession = getIsoAccession(featureName, entryAccession);
					}else throw new NextProtException("Something wrong occured when checking for iso specificity");
					
				}
				
					statementsMappedToEntryToLoad.addAll(transformStatements(originalStatement, sourceStatementsById, subjectStatements, entryAccession, isIsoSpecific, isoformSpecificAccession));
					
				}

		}
		
		return statementsMappedToEntryToLoad;
	
	}
	

	String loadStatements(Set<Statement> rawStatements, Set<Statement> mappedStatements, boolean load) {
		
		StringBuilder sb = new StringBuilder();

		try {
			
			if(load){
				
				addInfo(sb, "Loading raw statements: " + rawStatements.size());
				long start = System.currentTimeMillis();
				statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), NextProtSource.BioEditor);
				addInfo(sb, "Finish load in " + (System.currentTimeMillis() - start)/1000 + " seconds");
		
				addInfo(sb, "Loading entry statements: " + mappedStatements.size());
				start = System.currentTimeMillis();
				statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(mappedStatements, NextProtSource.BioEditor);
				addInfo(sb, "Finish load in " + (System.currentTimeMillis() - start)/1000 + " seconds");

			}else {
				addInfo(sb, "skipping load of " + rawStatements.size() + " raw statements and " + mappedStatements.size() + " mapped statements");
			}


		}catch (SQLException e){
			String errorResponse = "";
			errorResponse+= e.getMessage();
			if( e.getNextException() != null){
				errorResponse+= e.getNextException().getMessage();
			}
			return errorResponse;

		}
		
		return sb.toString();
	}

	
	@Override
	public String etlStatements(NextProtSource source, boolean load) {

		Set<Statement> rawStatements = extractStatements(source);
		Set<Statement> mappedStatements = transformStatements(rawStatements);
		return loadStatements(rawStatements, mappedStatements, load);
		
	}
	
	private String getIsoAccession (String featureName, String entryAccession){
		
		SequenceVariant sv;
		try {	
			sv = new SequenceVariant(featureName); 
		} catch (ParseException e) {
			throw new NextProtException(e);
		}

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
		Isoform isoSpecific = IsoformUtils.getIsoformByName(isoforms, sv.getIsoformName());
		return isoSpecific.getIsoformAccession();
		

	}
	
	private void addInfo (StringBuilder sb, String info){
		LOGGER.info(info);
		sb.append(info + "\n");
	}

	private Map<String, List<Statement>> getSubjectsTransformed(Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific) {

		//In case of entry variants have the target isoform property filled
		Map<String, List<Statement>> variantsOnIsoform = new HashMap<>();

		List<Statement> result = StatementTransformationUtil.getPropagatedStatementsForEntry(isoformMappingService, subjectStatements, nextprotAcession);
		variantsOnIsoform.put(nextprotAcession, result);
		
		return variantsOnIsoform;
	}
	
	Set<Statement> transformStatements(Statement originalStatement, Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific, String isoSpecificAccession){
		
		Set<Statement> statementsToLoad = new HashSet<>();

		//In case of entry variants have the target isoform property filled
		Map<String, List<Statement>> subjectsTransformedByEntryOrIsoform = getSubjectsTransformed(sourceStatementsById, subjectStatements, nextprotAcession, isIsoSpecific);
				
		for(Map.Entry<String, List<Statement>> entry : subjectsTransformedByEntryOrIsoform.entrySet()) {
				
				String entryOrIsoform = entry.getKey();
				List<Statement> subjects = entry.getValue();
				
				if(subjects.isEmpty()){
					throw new NextProtException("Empty subjects are not allowed");
				}
				
				String targetIsoformsForObject;
				String targetIsoformsForPhenotype;
				
					
				String entryAccession = subjects.get(0).getValue(StatementField.ENTRY_ACCESSION);

				List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
				NPreconditions.checkNotEmpty(isoforms, "Isoforms should not be null for " + entryAccession);
				
				List<String> isoformNames = isoforms.stream().map(Isoform::getIsoformAccession).collect(Collectors.toList());
				
				Set<TargetIsoformStatementPosition> targetIsoformsForPhenotypeSet = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(originalStatement, isoformMappingService, subjects, isIsoSpecific, isoSpecificAccession, isoformNames);
				targetIsoformsForPhenotype = TargetIsoformSerializer.serializeToJsonString(targetIsoformsForPhenotypeSet);

				//The same as for phenotype but without the name
				Set<TargetIsoformStatementPosition> targetIsoformsForObjectSet = new TreeSet<>();
				for(TargetIsoformStatementPosition tisp : targetIsoformsForPhenotypeSet){
					targetIsoformsForObjectSet.add(new TargetIsoformStatementPosition(tisp.getIsoformAccession(), tisp.getSpecificity(), null));
				}
				targetIsoformsForObject = TargetIsoformSerializer.serializeToJsonString(targetIsoformsForObjectSet);
				
				//Load objects
				Statement phenotypeIsoStatement;
				Statement objectIsoStatement = null;
				Statement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());
				
				if(objectStatement != null){

					objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForObject) // in case of entry
							.buildWithAnnotationHash(AnnotationType.ENTRY);
					
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
							.addSubjects(subjects).addObject(objectIsoStatement)							
							.removeField(StatementField.STATEMENT_ID) 
							.removeField(StatementField.SUBJECT_STATEMENT_IDS) 
							.removeField(StatementField.OBJECT_STATEMENT_IDS) 
							.buildWithAnnotationHash(AnnotationType.ENTRY);

				}else {
					
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
							.addSubjects(subjects)
							.removeField(StatementField.STATEMENT_ID) 
							.removeField(StatementField.SUBJECT_STATEMENT_IDS) 
							.removeField(StatementField.OBJECT_STATEMENT_IDS) 
							.buildWithAnnotationHash(AnnotationType.ENTRY);

				}


				//Load subjects
				statementsToLoad.addAll(subjects);
				
				//Load VPs
				statementsToLoad.add(phenotypeIsoStatement);
				
				//Load objects
				if(objectIsoStatement != null){
					statementsToLoad.add(objectIsoStatement);
				}


		}
		
		return statementsToLoad;

		
	}

	
	
	/**
	 * Returns an exception if there are mixes between subjects
	 * 
	 * @param subjects
	 * @return
	 */
	private static String validateSubject(Set<Statement> subjects) {

		Set<String> isoforms = subjects.stream().map(s -> {
			return s.getValue(StatementField.NEXTPROT_ACCESSION) + "-" + SequenceVariantUtils.getIsoformName(s.getValue(StatementField.ANNOTATION_NAME));
		}).collect(Collectors.toSet());

		if (isoforms.size() != 1) {
			throw new NextProtException("Mixing iso numbers for subjects is not allowed");
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

	public IsoformMappingService getIsoformMappingService() {
		return isoformMappingService;
	}

	public void setIsoformMappingService(IsoformMappingService isoformMappingService) {
		this.isoformMappingService = isoformMappingService;
	}


	public StatementLoaderService getStatementLoadService() {
		return statementLoadService;
	}


	public void setStatementLoadService(StatementLoaderService statementLoadService) {
		this.statementLoadService = statementLoadService;
	}



	public IsoformService getIsoformService() {
		return isoformService;
	}

	public void setIsoformService(IsoformService isoformService) {
		this.isoformService = isoformService;
	}


}
