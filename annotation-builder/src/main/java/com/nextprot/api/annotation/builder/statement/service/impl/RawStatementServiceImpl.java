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
import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementField;
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
	public List<IsoformAnnotation> getModifiedIsoformAnnotationsByIsoform(String nextprotAccession) {

		List<IsoformAnnotation> annotations = new ArrayList<>();

		List<RawStatement> phenotypeStatements = rawStatementDao.findPhenotypeRawStatements(nextprotAccession);

		Map<String, List<RawStatement>> impactStatementsBySubject = phenotypeStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(StatementField.SUBJECT_ANNOT_ISO_IDS)));

		impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {
			
			String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
			Set<IsoformAnnotation> subjectVariants = new TreeSet<IsoformAnnotation>(new Comparator<IsoformAnnotation>(){
				@Override
				public int compare(IsoformAnnotation o1, IsoformAnnotation o2) {
					return o1.getAnnotationUniqueName().compareTo(o2.getAnnotationUniqueName());
				}
			}); 

			for(String subjectComponentIdentifier : subjectComponentsIdentifiersArray){

				List<RawStatement> subjectVariantStatements = rawStatementDao.findRawStatementsByAnnotIsoId(subjectComponentIdentifier);
				if(subjectVariantStatements.isEmpty()){
					throw new NextProtException("Not found any variant for variant identifier:" + subjectComponentIdentifier);
				}
				IsoformAnnotation variant = AnnotationBuilder.buildAnnotation(nextprotAccession, subjectVariantStatements);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<RawStatement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
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
	

	@Cacheable("normal-annotations")
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
