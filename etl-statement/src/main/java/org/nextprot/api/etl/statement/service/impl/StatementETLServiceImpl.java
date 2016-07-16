package org.nextprot.api.etl.statement.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsondoc.core.annotation.ApiPathParam;
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

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;

@Service
public class StatementETLServiceImpl implements StatementETLService {

	@Autowired
	private RawStatementRemoteService statementRemoteService;

	@Autowired
	private IsoformMappingService isoformMappingService;

	private StatementLoaderService statementLoadService = new OracleStatementLoaderServiceImpl("MAPPED_STATEMENTS_NEXT");

	public String etlStatements(@ApiPathParam(name = "source", description = "The source to load from", allowedvalues = { "bioeditor" }) @PathVariable("category") String source) {

		List<RawStatement> sourceStatements = statementRemoteService.getStatementsForSource("bioeditor");
		System.err.println("Got response from source");
		Map<String, RawStatement> sourceStatementsById = sourceStatements.stream().collect(Collectors.toMap(RawStatement::getStatementId, Function.identity()));

		// List<RawStatement> sourceStatements =
		// statementRemoteService.getStatementsForSource("bioeditor");

		// Set<RawStatement> sourceStatementsWithAModifiedSubject =
		// sourceStatements.stream().filter(s -> s.getSubjectStatementIds() !=
		// null).collect(Collectors.asList());

		Set<RawStatement> statementsToLoad = new HashSet<RawStatement>();

		for (RawStatement originalStatement : sourceStatements) {

			String annotCat = originalStatement.getValue(StatementField.ANNOTATION_CATEGORY);

			if ("phenotype".equals(annotCat)) {


				String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();

				//TODO Multiple mutants!!!!!
				if (subjectStatemendIds.length == 1) {

					RawStatement variant = sourceStatementsById.get(subjectStatemendIds[0]);

					String nextprotAccession = variant.getValue(StatementField.NEXTPROT_ACCESSION);
					String feature = variant.getValue(StatementField.ANNOT_ISO_UNAME);
					boolean propagate = !feature.matches("\\w+-iso\\d-p.+");
					List<RawStatement> variantsOnIsoform = getPropagatedStatements(variant, variant.getValue(StatementField.ANNOT_ISO_UNAME), "variant", nextprotAccession, propagate);

					for(RawStatement isoSpecificVariant: variantsOnIsoform){
						
						String isoform = isoSpecificVariant.getValue(StatementField.ISOFORM_ACCESSION);

						RawStatement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());

						RawStatement objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement).addField(StatementField.ISOFORM_ACCESSION, isoform).build();

						Set<RawStatement> subjects = new HashSet<RawStatement>(Arrays.asList(isoSpecificVariant));
						
						RawStatement phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
								.addField(StatementField.ISOFORM_ACCESSION, isoform)
								.addAnnotationSubject(subjects)
								.addAnnotationObject(objectIsoStatement)
								.build();

						statementsToLoad.add(isoSpecificVariant);
						statementsToLoad.add(phenotypeIsoStatement);
						statementsToLoad.add(objectIsoStatement);

					}
					
	
				} else
					continue;

			}

		}

		System.err.println("Deleting...");
		statementLoadService.deleteAll();
		System.err.println("Loading"  + statementsToLoad.size());
		statementLoadService.load(statementsToLoad);
		System.err.println("Finished to load"  + statementsToLoad.size());

		return "yo";

	}

	public Set<RawStatement> findSourceStatementsWhereOriginalStatementIsUsedAsSubject(RawStatement originalStatement, Set<RawStatement> sourceStatementsWithAModifiedSubject) {
		return sourceStatementsWithAModifiedSubject.stream().filter(sm -> sm.getSubjectStatementIds().contains(originalStatement.getStatementId())).collect(Collectors.toSet());
	}

	private List<RawStatement> getPropagatedStatements(RawStatement variant, String feature, String annotCat, String nextprotAccession, boolean propagate) {

		List<RawStatement> result = new ArrayList<>();

		FeatureQueryResult featureQueryResult = isoformMappingService.propagateFeature(feature, annotCat, nextprotAccession);

		if (featureQueryResult.isSuccess()) {
			result.addAll(toRawStatementList(variant, (FeatureQuerySuccess) featureQueryResult));
		} else {
			FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
			System.err.println("Failure for " + variant.getStatementId() + " " + failure.getError().getMessage());
		}

		return result;

	}

	private List<RawStatement> toRawStatementList(RawStatement statement, FeatureQuerySuccess result) {

		List<RawStatement> rawStatementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			RawStatement rs = StatementBuilder.createNew().addMap(statement).addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformName())
					.addField(StatementField.RAW_STATEMENT_ID, statement.getStatementId()) // Keep
																							// a
																							// reference
																							// to
																							// the
																							// original
																							// statement
					.addField(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF, String.valueOf(isoformFeatureResult.getFirstIsoSeqPos()))
					.addField(StatementField.ANNOT_LOC_END_CANONICAL_REF, String.valueOf(isoformFeatureResult.getLastIsoSeqPos()))
					.addField(StatementField.ANNOT_ISO_UNAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature())).build();

			rawStatementList.add(rs);
		}

		return rawStatementList;
	}

}
