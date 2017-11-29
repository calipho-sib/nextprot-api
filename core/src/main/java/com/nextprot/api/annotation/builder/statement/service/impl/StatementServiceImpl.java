package com.nextprot.api.annotation.builder.statement.service.impl;

import com.nextprot.api.annotation.builder.EntryAnnotationBuilder;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import com.nextprot.api.annotation.builder.statement.service.StatementService;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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


	private List<Annotation> getProteoformEntryAnnotations(String entryAccession) {

		List<Statement> proteoformStatements = statementDao.findProteoformStatements(AnnotationType.ENTRY, entryAccession);

		//Collect all subjects
		List<String> subjectAnnotIds =  proteoformStatements.stream().map(s ->
			Arrays.asList(s.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(","))
		).flatMap(Collection::stream).collect(Collectors.toList());
		
		List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(AnnotationType.ENTRY, subjectAnnotIds);
		
		return EntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService).buildProteoformIsoformAnnotations(entryAccession, subjects, proteoformStatements);

	}


	private List<Annotation> getNormalEntryAnnotations(String entryAccession) {
		List<Statement> normalStatements = statementDao.findNormalStatements(AnnotationType.ENTRY, entryAccession);
		return EntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService).buildAnnotationList(entryAccession, normalStatements);
	}


	@Cacheable("statement-entry-annotations")
	@Override
	public List<Annotation> getAnnotations(String entryAccession) {

		List<Annotation> list = getProteoformEntryAnnotations(entryAccession);
		list.addAll(getNormalEntryAnnotations(entryAccession));

		return list;
	}
	
}
