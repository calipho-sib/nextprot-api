package org.nextprot.api.etl.statement.service.impl;

import java.util.ArrayList;
import java.util.List;

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

		List<RawStatement> sourceStatements = statementRemoteService.getStatementsForSourceForGeneName("bioeditor", "apc");
		List<RawStatement> loadedStatements = new ArrayList<>();

/*		for (RawStatement statement : sourceStatements) {

			String annotCat = statement.getValue(StatementField.ANNOTATION_CATEGORY);

			if ("variant".equals(annotCat)) {

				String nextprotAccession = statement.getValue(StatementField.NEXTPROT_ACCESSION);
				String feature = statement.getValue(StatementField.ANNOT_ISO_UNAME);

				boolean propagate = !feature.matches("\\w+-iso\\d-p.+");

				loadFeatures(statement, feature, annotCat, nextprotAccession, propagate, loadedStatements);
			} else {
				loadedStatements.add(statement);
			}
		}*/

		statementLoadService.deleteAll();
		statementLoadService.load(sourceStatements);

		return "yo";

	}

	private void loadFeatures(RawStatement statement, String feature, String annotCat, String nextprotAccession, boolean propagate, List<RawStatement> collect) {

		FeatureQueryResult featureQueryResult = isoformMappingService.propagateFeature(feature, annotCat, nextprotAccession);

		if (featureQueryResult.isSuccess()) {
			collect.addAll(toRawStatementList(statement, (FeatureQuerySuccess) featureQueryResult));
		}
		/*
		 * else { errors.add((FeatureQueryFailure) featureQueryResult); }
		 */
	}

	private List<RawStatement> toRawStatementList(RawStatement statement, FeatureQuerySuccess result) {

		List<RawStatement> rawStatementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			RawStatement rs = StatementBuilder.createNew().addMap(statement).addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformName())
					.addField(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF, String.valueOf(isoformFeatureResult.getFirstIsoSeqPos()))
					.addField(StatementField.ANNOT_LOC_END_CANONICAL_REF, String.valueOf(isoformFeatureResult.getLastIsoSeqPos()))
					.addField(StatementField.ANNOT_ISO_UNAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature())).build();

			rawStatementList.add(rs);
		}

		return rawStatementList;
	}

}
