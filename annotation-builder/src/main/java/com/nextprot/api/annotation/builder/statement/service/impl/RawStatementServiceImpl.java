package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioNormalAnnotation;
import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;
import com.nextprot.api.annotation.builder.statement.service.RawStatementService;

@Service
public class RawStatementServiceImpl implements RawStatementService {

	@Autowired
	public RawStatementDao rawStatementDao;

	//@Cacheable("modified-entry-annotations")
	public List<ModifiedEntry> getModifiedEntryAnnotation(String entryName) {
		
		List<ModifiedEntry> modifiedEntries = new ArrayList<ModifiedEntry>();
		List<RawStatement> impactstatements = rawStatementDao.findImpactRawStatements();
		
		Map<String, List<RawStatement>> impactStatementsByModifiedEntry =  
				impactstatements.stream().collect(Collectors.groupingBy(RawStatement::getBiological_subject_annot_hash));

		impactStatementsByModifiedEntry.keySet().forEach(subjectKey -> {

			//Subject (VD) , variant
			List<RawStatement> subjectVariantStatements = rawStatementDao.findRawStatementsByAnnotHash(subjectKey);
			Annotation subjectVariant = buildVariantAnnotation(subjectVariantStatements);
			
			//Impact annotations
			List<Annotation> impactAnnotations = buildAnnotationList(impactStatementsByModifiedEntry.get(subjectKey));

			ModifiedEntry me = new ModifiedEntry();
			me.setSubjectComponents(Arrays.asList(subjectVariant)); //TODO change this when multiple variants
			me.setAnnotations(impactAnnotations);
			modifiedEntries.add(me);
			
		});
		
		return modifiedEntries;
		
	}

	private static List<Annotation> buildAnnotationList(List<RawStatement> impactStatements) {
		
		List<Annotation> annotations = new ArrayList<>();
		Map<String, List<RawStatement>> impactStatementsByAnnotationHash =  
				impactStatements.stream().collect(Collectors.groupingBy(RawStatement::getAnnot_hash));

		impactStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			Annotation annotation = new Annotation();
			List<RawStatement> statements = impactStatementsByAnnotationHash.get(annotationHash);
			if(statements.size() != 1){
				System.err.println("ups getting " + statements.size() + " statements");
			}

			RawStatement statement = statements.get(0);
			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getAnnotation_category()));
			annotation.setCategory(category);
			

			annotation.setCvTermName(statement.getAnnot_cv_term_name());
			annotation.setCvTermAccessionCode(statement.getAnnot_cv_term_accession());
			//TODO this should be called terminology I guess! not setCVApiName
			annotation.setCvApiName(statement.getAnnot_cv_term_terminology());

			
			annotation.setAnnotationHash(statement.getAnnot_hash());
			if((statement.getBiological_object_annot_hash() != null) && (statement.getBiological_object_annot_hash().length() > 0) ){
				BioNormalAnnotation normalAnnotation = new BioNormalAnnotation();
				normalAnnotation.setAnnotationHash(statement.getBiological_object_annot_hash());
				annotation.setBioObject(normalAnnotation);
			}

			annotations.add(annotation);
			
		});
		return annotations;
	}

	private static Annotation buildVariantAnnotation(List<RawStatement> subjectVariantStatements) {
		Annotation annotation = new Annotation();
		
		if(subjectVariantStatements.size() != 1){
			System.err.println("ups getting " + subjectVariantStatements.size() + " variants");
		}
		RawStatement statement = subjectVariantStatements.get(0);
		
		String original = statement.getVariant_original_amino_acid();
		String variant = statement.getVariant_variation_amino_acid();
		String description = statement.getAnnot_name();
		
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);
		annotation.setDescription(description);
		annotation.setCategory(AnnotationCategory.VARIANT);
		return annotation;
	}

	@Override
	public List<Annotation> getNormalAnnotations(String entryName) {
		List<RawStatement> normalStatement = rawStatementDao.findNormalRawStatements();
		return buildAnnotationList(normalStatement);
	}

}
