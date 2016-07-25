package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.IsoformAnnotationBuilder;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import com.nextprot.api.annotation.builder.statement.service.StatementService;

@Service
public class StatementServiceImpl implements StatementService {

	private static final Logger LOGGER = Logger.getLogger(StatementServiceImpl.class);

	@Autowired
	public StatementDao statementDao;

	@Override
	public List<IsoformAnnotation> getProteoformIsoformAnnotations(String isoformAccession) {

		List<Statement> proteoformStatements = statementDao.findProteoformStatements(isoformAccession);

		//Collect all subjects
		List<String> subjectAnnotIds =  proteoformStatements.stream().map(s -> {
			return Arrays.asList(s.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(","));
		}).flatMap(l -> l.stream()).collect(Collectors.toList());
		
		List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(subjectAnnotIds);
		
		return IsoformAnnotationBuilder.buildProteoformIsoformAnnotations(isoformAccession, subjects, proteoformStatements);

	}
	

	@Override
	public List<IsoformAnnotation> getNormalIsoformAnnotations(String entryAccession) {
		List<Statement> normalStatements = statementDao.findNormalStatements(entryAccession);
		List<IsoformAnnotation> normalAnnotations = IsoformAnnotationBuilder.buildAnnotationList(entryAccession, normalStatements);
		normalAnnotations.stream().forEach(a -> {
			//Required for group by
			a.setSubjectName(entryAccession);
		});
		return normalAnnotations;
	}


	@Override
	public List<Annotation> getProteoformEntryAnnotations(String entryAccession) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Annotation> getNormalEntryAnnotations(String entryAccession) {
		// TODO Auto-generated method stub
		return null;
	}

}
