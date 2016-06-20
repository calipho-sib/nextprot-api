package com.nextprot.api.annotation.builder.statement.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioGenericObject;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
			List<IsoformAnnotation> variants = buildAnnotationList(entryName + "-1" , subjectVariantStatements);
			if(variants.size() != 1){
				LOGGER.error("Found more or less than one variant for a given subject" + subjectAnnotationHash);
			}

			// Impact annotations
			List<RawStatement> impactStatements = impactStatementsBySubject.get(subjectAnnotationHash);
			List<IsoformAnnotation> impactAnnotations = buildAnnotationList(entryName + "-1", impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				ia.setSubjectName(entryName + "-1 " + variants.get(0).getAnnotationUniqueName());
				ia.setSubjectComponents(Arrays.asList(subjectAnnotationHash));
			});

			annotations.addAll(impactAnnotations);

		});

		return annotations;

	}

	private static List<AnnotationEvidence> buildAnnotationEvidences(List<RawStatement> rawStatements) {
		return rawStatements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setResourceAssociationType("evidence");
			if (s.getExp_context_property_intensity() != null) {
				AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
				prop.setPropertyName("intensity");
				prop.setPropertyValue(s.getExp_context_property_intensity());
				evidence.setProperties(Arrays.asList(prop));
			}
			return evidence;
		}).collect(Collectors.toList());

	}

	private static List<IsoformAnnotation> buildAnnotationList(String isoformName, List<RawStatement> flatStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<RawStatement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(RawStatement::getAnnot_hash));

		flatStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			IsoformAnnotation isoAnnotation = new IsoformAnnotation();
			List<RawStatement> statements = flatStatementsByAnnotationHash.get(annotationHash);

			RawStatement statement = statements.get(0);

			isoAnnotation.setEvidences(buildAnnotationEvidences(statements));

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getAnnotation_category()));
			isoAnnotation.setCategory(category);
			
			if(category.equals(AnnotationCategory.VARIANT)) 
				setVariantAttributes(isoAnnotation, statement);

			isoAnnotation.setIsoformName(isoformName);
			isoAnnotation.setCvTermName(statement.getAnnot_cv_term_name());
			isoAnnotation.setDescription(statement.getAnnot_description());
			isoAnnotation.setCvTermAccessionCode(statement.getAnnot_cv_term_accession());
			// TODO this should be called terminology I guess! not setCVApiName
			isoAnnotation.setCvApiName(statement.getAnnot_cv_term_terminology());
			isoAnnotation.setAnnotationUniqueName(statement.getAnnot_name());

			isoAnnotation.setAnnotationHash(statement.getAnnot_hash());
			if ((statement.getBiological_object_annot_hash() != null) && (statement.getBiological_object_annot_hash().length() > 0)
					|| (statement.getBiological_object_accession() != null && (statement.getBiological_object_accession().length() > 0))) {

				BioGenericObject bioObject = new BioGenericObject();
				bioObject.setAccession(statement.getBiological_object_accession()); // In
																					// case
																					// of
																					// interactions
				bioObject.setType(statement.getBiological_object_type());
				bioObject.setAnnotationHash(statement.getBiological_object_annot_hash()); // In
																							// case
																							// of
																							// phenotypes
				isoAnnotation.setBioObject(bioObject);

			}

			annotations.add(isoAnnotation);

		});
		return annotations;
	}

	private static void setVariantAttributes(IsoformAnnotation annotation, RawStatement variantStatement) {

		String original = variantStatement.getVariant_original_amino_acid();
		String variant = variantStatement.getVariant_variation_amino_acid();
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);

		try {
			Integer positionBeginCanononical = Integer.valueOf(variantStatement.getAnnot_loc_begin_canonical_ref());
			annotation.setLocationCanonicalBegin(positionBeginCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert begin position " + variantStatement.getAnnot_loc_begin_canonical_ref());
		}

		try {
			Integer positionEndCanononical = Integer.valueOf(variantStatement.getAnnot_loc_end_canonical_ref());
			annotation.setLocationCanonicalEnd(positionEndCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert end position " + variantStatement.getAnnot_loc_begin_canonical_ref());
		}

	}

	@Override
	public List<IsoformAnnotation> getNormalAnnotations(String entryName) {
		List<RawStatement> normalStatements = rawStatementDao.findNormalRawStatements(entryName);
		List<IsoformAnnotation> normalAnnotations = buildAnnotationList(entryName + "-1", normalStatements);
		normalAnnotations.stream().forEach(a -> {a.setSubjectName(entryName + "-1");});
		return normalAnnotations;
	}

}
