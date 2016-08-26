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
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementRemoteService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.nextprot.commons.statements.service.StatementLoaderService;
import org.nextprot.commons.statements.service.impl.JDBCStatementLoaderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;
import com.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;

@Service
public class StatementETLServiceImpl implements StatementETLService {
	
	private static final Logger LOGGER = Logger.getLogger(StatementETLServiceImpl.class);

	@Autowired private IsoformService isoformService;
	
	@Autowired
	private StatementRemoteService statementRemoteService;

	@Autowired
	private IsoformMappingService isoformMappingService;

	private StatementLoaderService statementLoadService = new JDBCStatementLoaderServiceImpl();

	
	List<Statement> extractStatements(NextProtSource source) {
		List<Statement> sourceStatements = statementRemoteService.getStatementsForSource(source);
		//List<Statement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName(NextProtSource.BioEditor, "scn9a");
		//List<Statement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName(NextProtSource.BioEditor, "scn9a");
		return sourceStatements;
		
	}

		
	@Override
	public String etlStatements(NextProtSource source) {

		List<Statement> rawStatements = extractStatements(source);

		//System.err.println("Got response from source");
		Map<String, Statement> sourceStatementsById = rawStatements.stream().collect(Collectors.toMap(Statement::getStatementId, Function.identity()));

		//Set<Statement> statementsMappedToIsoformToLoad = new HashSet<Statement>();
		Set<Statement> statementsMappedToEntryToLoad = new HashSet<Statement>();

		for (Statement originalStatement : rawStatements) {

			if ((originalStatement.getSubjectStatementIds() != null) && (!originalStatement.getSubjectStatementIds().isEmpty())) {

				String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();
				Set<Statement> subjectStatements = getSubjects(subjectStatemendIds, sourceStatementsById);

				String entryAccession = subjectStatements.iterator().next().getValue(StatementField.ENTRY_ACCESSION);

				boolean isIsoSpecific = false;
				String isoformName = null;
				String isoformSpecificAccession = null;

				if (isSubjectIsoSpecific(subjectStatements)) {
					isoformName = checkThatSubjectsAreOnSameIsoform(subjectStatements);
					if(isoformName != null){
						isIsoSpecific = true;
						String featureName = subjectStatements.iterator().next().getValue(StatementField.ANNOTATION_NAME);
						isoformSpecificAccession = getIsoAccession(featureName, entryAccession);
					}else throw new NextProtException("Something wrong occured when checking for iso specificity");
				}
				
					//statementsMappedToIsoformToLoad.addAll(transformStatements(AnnotationType.ISOFORM, originalStatement, sourceStatementsById, subjectStatements, entryAccession, isIsoSpecific));
					statementsMappedToEntryToLoad.addAll(transformStatements(AnnotationType.ENTRY, originalStatement, sourceStatementsById, subjectStatements, entryAccession, isIsoSpecific, isoformSpecificAccession));
					
				}

		}

		StringBuilder sb = new StringBuilder();

		try {
			
			addInfo(sb, "Loading raw statements: " + rawStatements.size());
			statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), NextProtSource.BioEditor);

			/*addInfo(sb, "Loading iso statements: " + statementsMappedToIsoformToLoad.size());
			statementLoadService.loadStatementsMappedToIsoSpecAnnotationsForSource(statementsMappedToIsoformToLoad, NextProtSource.BioEditor);*/

			addInfo(sb, "Loading entry statements: " + statementsMappedToEntryToLoad.size());
			statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(statementsMappedToEntryToLoad, NextProtSource.BioEditor);


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
	
	private String getIsoAccession (String featureName, String entryAccession){
		
		SequenceVariant sv;
		try {	sv = new SequenceVariant(featureName); } 
		catch (ParseException e) {
			e.printStackTrace(); throw new NextProtException(e.getMessage());
		};

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
		Isoform isoSpecific = IsoformUtils.getIsoformByName(isoforms, sv.getIsoformName());
		return isoSpecific.getIsoformAccession();
		

	}
	
	private void addInfo (StringBuilder sb, String info){
		System.err.println(info);
		sb.append(info + "\n");
		
	}

	private Map<String, List<Statement>> getSubjectsTransformed(AnnotationType type, Statement originalStatement, Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific) {

		//In case of entry variants have the target isoform property filled
		Map<String, List<Statement>> variantsOnIsoform = new HashMap<>();

		if(type.equals(AnnotationType.ENTRY)){
			List<Statement> result = StatementTransformationUtil.getPropagatedStatementsForEntry(isoformMappingService, subjectStatements, nextprotAcession);
			variantsOnIsoform.put(nextprotAcession, result);
		}else {
			variantsOnIsoform = StatementTransformationUtil.getPropagatedStatementsForIsoform(isoformMappingService, subjectStatements, nextprotAcession, !isIsoSpecific);
		}
		
		return variantsOnIsoform;
	}
	
	Set<Statement> transformStatements(AnnotationType type, Statement originalStatement, Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific, String isoSpecificAccession){
		
		Set<Statement> statementsToLoad = new HashSet<Statement>();

		//In case of entry variants have the target isoform property filled
		Map<String, List<Statement>> subjectsTransformedByEntryOrIsoform = getSubjectsTransformed(type, originalStatement, sourceStatementsById, subjectStatements, nextprotAcession, isIsoSpecific);
				
		for(String entryOrIsoform : subjectsTransformedByEntryOrIsoform.keySet()) {
				
				List<Statement> subjects = subjectsTransformedByEntryOrIsoform.get(entryOrIsoform);
				
				if(subjects.isEmpty()){
					LOGGER.warn("subjects is empty, skip");
					continue;
				}
				
				String targetIsoformsForObject = null;
				String targetIsoformsForPhenotype = null;
				
				if(type.equals(AnnotationType.ENTRY)){
					
					String entryAccession = subjects.get(0).getValue(StatementField.ENTRY_ACCESSION);

					List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
					List<String> isoformNames = isoforms.stream().map(Isoform::getIsoformAccession).collect(Collectors.toList());
					
					Set<TargetIsoformStatementPosition> targetIsoformsForPhenotypeSet = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(originalStatement, isoformMappingService, subjects, isIsoSpecific, isoSpecificAccession, isoformNames);
					targetIsoformsForPhenotype = TargetIsoformSerializer.serializeToJsonString(targetIsoformsForPhenotypeSet);

					//The same as for phenotype but without the name
					Set<TargetIsoformStatementPosition> targetIsoformsForObjectSet = new TreeSet<TargetIsoformStatementPosition>();
					for(TargetIsoformStatementPosition tisp : targetIsoformsForPhenotypeSet){
						targetIsoformsForObjectSet.add(new TargetIsoformStatementPosition(tisp.getIsoformAccession(), tisp.getSpecificity(), null));
					}
					targetIsoformsForObject = TargetIsoformSerializer.serializeToJsonString(targetIsoformsForObjectSet);
					
					
				}
				
				
				//Load objects
				Statement phenotypeIsoStatement =  null;
				Statement objectIsoStatement = null;
				Statement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());
				
				if(objectStatement != null){

					objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForObject) // in case of entry
							.build();
					
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
							.addSubjects(subjects).addObject(objectIsoStatement).build();
				}else {
					
					phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
							.addField(StatementField.ISOFORM_ACCESSION, entryOrIsoform) //in case of isoform
							.addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
							.addSubjects(subjects).build();

				}


				//Load subjects
				subjects.forEach(s -> statementsToLoad.add(s));
				
				//Load VPs
				statementsToLoad.add(phenotypeIsoStatement);
				
				//Load objects
				if(objectIsoStatement != null){
					statementsToLoad.add(objectIsoStatement);
				}


		};
		
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

	public IsoformMappingService getIsoformMappingService() {
		return isoformMappingService;
	}

	public void setIsoformMappingService(IsoformMappingService isoformMappingService) {
		this.isoformMappingService = isoformMappingService;
	}


}
