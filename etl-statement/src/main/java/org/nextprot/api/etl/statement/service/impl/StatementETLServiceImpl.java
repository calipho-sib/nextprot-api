package org.nextprot.api.etl.statement.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.jsondoc.core.annotation.ApiPathParam;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.statement.service.RawStatementRemoteService;
import org.nextprot.api.etl.statement.service.StatementETLService;
import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.service.StatementLoaderService;
import org.nextprot.commons.statements.service.impl.OracleStatementLoaderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatementETLServiceImpl implements StatementETLService {

	@Autowired
	private RawStatementRemoteService statementRemoteService;

	@Autowired
	private IsoformMappingService isoformMappingService;

	private StatementLoaderService statementLoadService = new OracleStatementLoaderServiceImpl("MAPPED_STATEMENTS_NEXT");

	public String etlStatements(@ApiPathParam(name = "source", description = "The source to load from", allowedvalues = { "bioeditor" }) @PathVariable("category") String source) {

		//List<RawStatement> sourceStatements = statementRemoteService.getStatementsForSource("bioeditor");
		List<RawStatement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName("bioeditor", "scn9a");

		//System.err.println("Got response from source");
		Map<String, RawStatement> sourceStatementsById = sourceStatements.stream().collect(Collectors.toMap(RawStatement::getStatementId, Function.identity()));

		Set<RawStatement> statementsToLoad = new HashSet<RawStatement>();

		for (RawStatement originalStatement : sourceStatements) {

			String annotCat = originalStatement.getValue(StatementField.ANNOTATION_CATEGORY);

			if ("phenotype".equals(annotCat)) {

					
				if(originalStatement.getStatementId().equals("2867942d33772889f62812b24b0f8275")){
					System.out.println("Yo");
				}

				String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();

				Set<RawStatement> subjectStatements = getSubjects(subjectStatemendIds, sourceStatementsById);

				String nextprotAcession = subjectStatements.iterator().next().getValue(StatementField.NEXTPROT_ACCESSION);

				boolean isIsoSpecific = false;
				if (isSubjectIsoSpecific(subjectStatements)) {
					nextprotAcession = checkThatSubjectsAreOnSameIsoform(subjectStatements);
					isIsoSpecific = true;
				}

				boolean propagate = !isIsoSpecific;
				
				Map<String, List<RawStatement>> variantsOnIsoform = getPropagatedStatements(subjectStatements, nextprotAcession, propagate);
				
				variantsOnIsoform.keySet().stream().forEach(isoform -> {
						
						List<RawStatement> subjects = variantsOnIsoform.get(isoform);

						RawStatement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());

						RawStatement objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).build();

						RawStatement phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).addAnnotationSubject(subjects)
								.addAnnotationObject(objectIsoStatement).build();


						//Load subjects
						subjects.forEach(s -> statementsToLoad.add(s));
						
						//Load phenotypes
						statementsToLoad.add(phenotypeIsoStatement);
						
						//Load objects
						statementsToLoad.add(objectIsoStatement);


				});

			}

		}

		System.err.println("Deleting...");
		statementLoadService.deleteAll();
		System.err.println("Loading" + statementsToLoad.size());
		statementLoadService.load(statementsToLoad);
		System.err.println("Finished to load" + statementsToLoad.size());

		return "yo";

	}

	/**
	 * Returns an exception if there are mixes between subjects
	 * 
	 * @param subjects
	 * @return
	 */
	private static String checkThatSubjectsAreOnSameIsoform(Set<RawStatement> subjects) {

		Set<String> isoforms = subjects.stream().map(s -> {
			return s.getValue(StatementField.NEXTPROT_ACCESSION) + "-" + SequenceVariantUtils.getIsoformNumber(s.getValue(StatementField.ANNOT_ISO_UNAME));
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
	private static boolean isSubjectIsoSpecific(Set<RawStatement> subjects) {
		int isoSpecificSize = subjects.stream().filter(s -> SequenceVariantUtils.isIsoSpecific(s.getValue(StatementField.ANNOT_ISO_UNAME))).collect(Collectors.toList()).size();
		if (isoSpecificSize == 0) {
			return false;
		} else if (isoSpecificSize == subjects.size()) {
			return true;
		} else {
			throw new NextProtException("Mixing iso specific subjects with non-iso specific variants is not allowed");
		}
	}

	private static Set<RawStatement> getSubjects(String[] subjectIds, Map<String, RawStatement> sourceStatementsById) {
		Set<RawStatement> variants = new HashSet<>();
		for (String subjectId : subjectIds) {
			RawStatement subjectStatement = sourceStatementsById.get(subjectId);
			if (subjectStatement == null) {
				throw new NextProtException("Subject " + subjectId + " not present in the given list");
			}
			variants.add(subjectStatement);
		}
		return variants;
	}

	public Set<RawStatement> findSourceStatementsWhereOriginalStatementIsUsedAsSubject(RawStatement originalStatement, Set<RawStatement> sourceStatementsWithAModifiedSubject) {
		return sourceStatementsWithAModifiedSubject.stream().filter(sm -> sm.getSubjectStatementIds().contains(originalStatement.getStatementId())).collect(Collectors.toSet());
	}

	private Map<String, List<RawStatement>> getPropagatedStatements(Set<RawStatement> multipleSubjects, String nextprotAccession, boolean propagate) {

		List<RawStatement> result = new ArrayList<>();

		for (RawStatement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult = null;
			if(propagate){
				featureQueryResult = isoformMappingService.propagateFeature(subject.getValue(StatementField.ANNOT_ISO_UNAME), "variant", nextprotAccession);
			}else {
				featureQueryResult = isoformMappingService.validateFeature(subject.getValue(StatementField.ANNOT_ISO_UNAME), "variant", nextprotAccession);
			}

			if (featureQueryResult.isSuccess()) {
				result.addAll(toRawStatementList(subject, (FeatureQuerySuccess) featureQueryResult));

			} else {
				FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
				System.err.println("Failure for " + subject.getStatementId() + " " + failure.getError().getMessage());
			}
		}

		// Group the subjects by isoform
		Map<String, List<RawStatement>> subjectsByIsoform = result.stream().collect(Collectors.groupingBy(s -> (String) s.getValue(StatementField.ISOFORM_ACCESSION)));

		// Filter only subjects that contain all original subjects (the size is
		// the same). In other words, if 2 multiples mutants can not be mapped
		// to all isoform, the statement is not valid
		return subjectsByIsoform.entrySet().stream().filter(map -> map.getValue().size() == multipleSubjects.size()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

	}

	private List<RawStatement> toRawStatementList(RawStatement statement, FeatureQuerySuccess result) {

		List<RawStatement> rawStatementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			if (isoformFeatureResult.isMapped()) {

				RawStatement rs = StatementBuilder.createNew().addMap(statement).addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformName())
						.addField(StatementField.RAW_STATEMENT_ID, statement.getStatementId()) // Keep  a reference to the original statement
						.addField(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF, String.valueOf(isoformFeatureResult.getBeginIsoformPosition()))
						.addField(StatementField.ANNOT_LOC_END_CANONICAL_REF, String.valueOf(isoformFeatureResult.getEndIsoformPosition()))
						.addField(StatementField.ANNOT_LOC_BEGIN_GENOMIC_REF, String.valueOf(isoformFeatureResult.getBeginMasterPosition()))
						.addField(StatementField.ANNOT_LOC_END_GENOMIC_REF, String.valueOf(isoformFeatureResult.getEndMasterPosition()))
						.addField(StatementField.ISOFORM_CANONICAL, String.valueOf(isoformFeatureResult.isCanonical()))
						.addField(StatementField.ANNOT_ISO_UNAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature())).build();

				rawStatementList.add(rs);

			}

		}

		return rawStatementList;
	}

}
