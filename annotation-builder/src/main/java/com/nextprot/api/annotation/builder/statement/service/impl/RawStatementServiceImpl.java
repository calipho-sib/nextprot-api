package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.AnnotationBuilder;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import com.nextprot.api.annotation.builder.statement.service.RawStatementService;

@Service
public class RawStatementServiceImpl implements RawStatementService {

	private static final Logger LOGGER = Logger.getLogger(RawStatementServiceImpl.class);

	@Autowired
	public StatementDao statementDao;

	@Override
	public List<IsoformAnnotation> getModifiedIsoformAnnotationsByIsoform(String nextprotAccession) {

		List<IsoformAnnotation> annotations = new ArrayList<>();

		List<Statement> proteoformStatements = statementDao.findPhenotypeStatements(nextprotAccession);

		List<String> subjectAnnotIds =  proteoformStatements.stream().map(s -> {
			
			String annotIds = s.getValue(StatementField.SUBJECT_ANNOTATION_IDS);
			
			return Arrays.asList(annotIds.split(","));
			
			
		}).flatMap(l -> l.stream()).collect(Collectors.toList());
		
		List<Statement> subjects = statementDao.findStatementsByAnnotIsoIds(subjectAnnotIds);
		Map<String, List<Statement>> subjectsByAnnotationId = subjects.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		Map<String, List<Statement>> impactStatementsBySubject = proteoformStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(StatementField.SUBJECT_ANNOTATION_IDS)));

		impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {
			
			String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
			Set<IsoformAnnotation> subjectVariants = new TreeSet<IsoformAnnotation>(new Comparator<IsoformAnnotation>(){
				@Override
				public int compare(IsoformAnnotation o1, IsoformAnnotation o2) {
					return o1.getAnnotationUniqueName().compareTo(o2.getAnnotationUniqueName());
				}
			}); 

			for(String subjectComponentIdentifier : subjectComponentsIdentifiersArray){

				List<Statement> subjectVariant = subjectsByAnnotationId.get(subjectComponentIdentifier);
				
				if((subjectVariant == null) || (subjectVariant.isEmpty())){
					throw new NextProtException("Not found any subject  identifier:" + subjectComponentIdentifier);
				}
				IsoformAnnotation variant = AnnotationBuilder.buildAnnotation(nextprotAccession, subjectVariant);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<Statement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
			List<IsoformAnnotation> impactAnnotations = AnnotationBuilder.buildAnnotationList(nextprotAccession, impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				
				String name = subjectVariants.stream().map(v -> v.getAnnotationUniqueName()).collect(Collectors.joining(" + ")).toString();
				
				ia.setSubjectName(nextprotAccession + " " + name);
				ia.setSubjectComponents(Arrays.asList(subjectComponentsIdentifiersArray));
			});

			annotations.addAll(impactAnnotations);

		});

		return annotations;

	}
	

	@Override
	public List<IsoformAnnotation> getNormalAnnotations(String entryName) {
		List<Statement> normalStatements = statementDao.findNormalStatements(entryName);
		List<IsoformAnnotation> normalAnnotations = AnnotationBuilder.buildAnnotationList(entryName + "-1", normalStatements);
		normalAnnotations.stream().forEach(a -> {
			a.setSubjectName(entryName + "-1");
		});
		return normalAnnotations;
	}

}
