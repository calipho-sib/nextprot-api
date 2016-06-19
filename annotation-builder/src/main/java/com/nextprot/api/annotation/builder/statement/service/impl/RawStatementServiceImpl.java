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
import org.nextprot.api.core.domain.ModifiedEntry;
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

	private Logger logger = Logger.getLogger(RawStatementServiceImpl.class);

	@Autowired
	public RawStatementDao rawStatementDao;

	@Cacheable("modified-entry-annotations")
	public List<ModifiedEntry> getModifiedEntryAnnotation(String entryName) {

		List<ModifiedEntry> modifiedEntries = new ArrayList<ModifiedEntry>();
		List<RawStatement> impactstatements = rawStatementDao.findPhenotypeRawStatements(entryName);

		Map<String, List<RawStatement>> impactStatementsByModifiedEntry = impactstatements.stream().collect(Collectors.groupingBy(RawStatement::getBiological_subject_annot_hash));

		impactStatementsByModifiedEntry.keySet().forEach(subjectKey -> {

			// Subject (VD) , variant
			List<RawStatement> subjectVariantStatements = rawStatementDao.findRawStatementsByAnnotHash(subjectKey);
			IsoformAnnotation subjectVariant = buildVariantAnnotation(subjectVariantStatements);

			System.err.println("");

			if (subjectVariant == null) {
				logger.error("Did not found variants for hash: " + subjectKey);
			} else {

				// Impact annotations
				List<IsoformAnnotation> impactAnnotations = buildAnnotationList(impactStatementsByModifiedEntry.get(subjectKey));

				ModifiedEntry me = new ModifiedEntry();
				me.setSubjectComponents(Arrays.asList(subjectVariant)); // TODO
																		// change
																		// this
																		// when
																		// multiple
																		// variants
				me.setAnnotations(impactAnnotations);
				modifiedEntries.add(me);

			}

		});

		return modifiedEntries;

	}

	private static List<AnnotationEvidence> buildAnnotationEvidences(List<RawStatement> rawStatements) {
		return rawStatements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setResourceAssociationType("evidence");
			if(s.getExp_context_property_intensity() != null){
				AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
				prop.setPropertyName("intensity");
				prop.setPropertyValue(s.getExp_context_property_intensity());
				evidence.setProperties(Arrays.asList(prop));
			}
			return evidence;
		}).collect(Collectors.toList());
		
	}

	private static List<IsoformAnnotation> buildAnnotationList(List<RawStatement> flatStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<RawStatement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(RawStatement::getAnnot_hash));

		flatStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			IsoformAnnotation isoAnnotation = new IsoformAnnotation();
			List<RawStatement> statements = flatStatementsByAnnotationHash.get(annotationHash);

			RawStatement statement = statements.get(0);
			
			isoAnnotation.setEvidences(buildAnnotationEvidences(statements));

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getAnnotation_category()));
			isoAnnotation.setCategory(category);

			isoAnnotation.setCvTermName(statement.getAnnot_cv_term_name());
			isoAnnotation.setDescription(statement.getAnnot_description());
			isoAnnotation.setCvTermAccessionCode(statement.getAnnot_cv_term_accession());
			// TODO this should be called terminology I guess! not setCVApiName
			isoAnnotation.setCvApiName(statement.getAnnot_cv_term_terminology());

			isoAnnotation.setAnnotationHash(statement.getAnnot_hash());
			if ((statement.getBiological_object_annot_hash() != null) && (statement.getBiological_object_annot_hash().length() > 0) ||
				(statement.getBiological_object_accession() != null && (statement.getBiological_object_accession().length() > 0))) {

					BioGenericObject bioObject = new BioGenericObject();
					bioObject.setAccession(statement.getBiological_object_accession()); // In case of interactions
					bioObject.setType(statement.getBiological_object_type());
					bioObject.setAnnotationHash(statement.getBiological_object_annot_hash()); // In case of phenotypes
					isoAnnotation.setBioObject(bioObject);

			}

			annotations.add(isoAnnotation);

		});
		return annotations;
	}

	private static IsoformAnnotation buildVariantAnnotation(List<RawStatement> subjectVariantStatements) {
		IsoformAnnotation isoAnnotation = new IsoformAnnotation();

		if (subjectVariantStatements.size() != 1) {
			System.err.println("ups getting " + subjectVariantStatements.size() + " variants");
			return null;
		}
		RawStatement statement = subjectVariantStatements.get(0);

		String original = statement.getVariant_original_amino_acid();
		String variant = statement.getVariant_variation_amino_acid();

		try {
			Integer positionBeginCanononical = Integer.valueOf(statement.getAnnot_loc_begin_canonical_ref());
			isoAnnotation.setLocationCanonicalBegin(positionBeginCanononical);
		} catch (Exception e) {
		}

		try {
			Integer positionEndCanononical = Integer.valueOf(statement.getAnnot_loc_end_canonical_ref());
			isoAnnotation.setLocationCanonicalBegin(positionEndCanononical);
		} catch (Exception e) {
		}

		String description = statement.getAnnot_name();

		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		isoAnnotation.setVariant(annotationVariant);
		isoAnnotation.setDescription(description);
		isoAnnotation.setCategory(AnnotationCategory.VARIANT);
		return isoAnnotation;
	}

	@Override
	public List<IsoformAnnotation> getNormalAnnotations(String entryName) {
		List<RawStatement> normalStatement = rawStatementDao.findNormalRawStatements(entryName);
		return buildAnnotationList(normalStatement);
	}

}
