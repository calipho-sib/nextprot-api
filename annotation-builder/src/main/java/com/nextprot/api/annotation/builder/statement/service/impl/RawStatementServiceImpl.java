package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.AnnotationBuilder;
import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;
import com.nextprot.api.annotation.builder.statement.service.RawStatementService;

@Service
public class RawStatementServiceImpl implements RawStatementService {

	private static final Logger LOGGER = Logger.getLogger(RawStatementServiceImpl.class);

	@Autowired
	public RawStatementDao rawStatementDao;

	@Cacheable("modified-entry-annotations")
	@Override
	public List<IsoformAnnotation> getModifiedIsoformAnnotationsByIsoform(String entryName) {

		List<IsoformAnnotation> annotations = new ArrayList<>();

		List<RawStatement> phenotypeStatements = rawStatementDao.findPhenotypeRawStatements(entryName);

		Map<String, List<RawStatement>> impactStatementsBySubject = phenotypeStatements.stream().collect(Collectors.groupingBy(RawStatement::getBiological_subject_annot_hash));

		impactStatementsBySubject.keySet().forEach(subjectAnnotationHash -> {

			List<RawStatement> subjectVariantStatements = rawStatementDao.findRawStatementsByAnnotHash(subjectAnnotationHash);
			List<IsoformAnnotation> variants = AnnotationBuilder.buildAnnotationList(entryName + "-1", subjectVariantStatements);
			if (variants.size() != 1) {
				LOGGER.error("Found more or less than one variant for a given subject" + subjectAnnotationHash);
			}

			// Impact annotations
			List<RawStatement> impactStatements = impactStatementsBySubject.get(subjectAnnotationHash);
			List<IsoformAnnotation> impactAnnotations = AnnotationBuilder.buildAnnotationList(entryName + "-1", impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				ia.setSubjectName(entryName + "-1 " + variants.get(0).getAnnotationUniqueName());
				ia.setSubjectComponents(Arrays.asList(subjectAnnotationHash));
			});

			annotations.addAll(impactAnnotations);

		});

		return annotations;

	}


	@Override
	public List<IsoformAnnotation> getNormalAnnotations(String entryName) {
		List<RawStatement> normalStatements = rawStatementDao.findNormalRawStatements(entryName);
		List<IsoformAnnotation> normalAnnotations = AnnotationBuilder.buildAnnotationList(entryName + "-1", normalStatements);
		normalAnnotations.stream().forEach(a -> {
			a.setSubjectName(entryName + "-1");
		});
		return normalAnnotations;
	}

}
