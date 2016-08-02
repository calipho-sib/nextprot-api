package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.EntryAnnotationBuilder;
import com.nextprot.api.annotation.builder.IsoformAnnotationBuilder;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import com.nextprot.api.annotation.builder.statement.service.StatementService;

@Service
public class StatementServiceImpl implements StatementService {

	private static final Logger LOGGER = Logger.getLogger(StatementServiceImpl.class);

	@Autowired
	public StatementDao statementDao;

	@Autowired
	public TerminologyService terminologyService;
	
	@Cacheable("proteoform-iso-annotations")
	@Override
	public List<IsoformAnnotation> getProteoformIsoformAnnotations(String isoformAccession) {

		List<Statement> proteoformStatements = statementDao.findProteoformStatements(AnnotationType.ISOFORM, isoformAccession);

		//Collect all subjects
		List<String> subjectAnnotIds =  proteoformStatements.stream().map(s -> {
			return Arrays.asList(s.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(","));
		}).flatMap(l -> l.stream()).collect(Collectors.toList());
		
		List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(AnnotationType.ISOFORM, subjectAnnotIds);
		
		return IsoformAnnotationBuilder.newBuilder(terminologyService).buildProteoformIsoformAnnotations(isoformAccession, subjects, proteoformStatements);

	}
	

	@Cacheable("normal-iso-annotations")
	@Override
	public List<IsoformAnnotation> getNormalIsoformAnnotations(String entryAccession) {
		List<Statement> normalStatements = statementDao.findNormalStatements(AnnotationType.ISOFORM, entryAccession);
		List<IsoformAnnotation> normalAnnotations = IsoformAnnotationBuilder.newBuilder(terminologyService).buildAnnotationList(entryAccession, normalStatements);
		normalAnnotations.stream().forEach(a -> 
			//Required for group by
			a.setSubjectName(entryAccession)
		);
		return normalAnnotations;
	}


	@Override
	public List<Annotation> getProteoformEntryAnnotations(String entryAccession) {

		List<Statement> proteoformStatements = statementDao.findProteoformStatements(AnnotationType.ENTRY, entryAccession);

		//Collect all subjects
		List<String> subjectAnnotIds =  proteoformStatements.stream().map(s -> {
			return Arrays.asList(s.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(","));
		}).flatMap(l -> l.stream()).collect(Collectors.toList());
		
		List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(AnnotationType.ENTRY, subjectAnnotIds);
		
		return EntryAnnotationBuilder.newBuilder(terminologyService).buildProteoformIsoformAnnotations(entryAccession, subjects, proteoformStatements);

	}


	@Override
	public List<Annotation> getNormalEntryAnnotations(String entryAccession) {
		List<Statement> normalStatements = statementDao.findNormalStatements(AnnotationType.ENTRY, entryAccession);
		List<Annotation>  annotations =  EntryAnnotationBuilder.newBuilder(terminologyService).buildAnnotationList(entryAccession, normalStatements);
		return annotations;
		
	}


	@Override
	public List<Annotation> getAnnotations(String entryAccession) {
		return null;
	}

}
