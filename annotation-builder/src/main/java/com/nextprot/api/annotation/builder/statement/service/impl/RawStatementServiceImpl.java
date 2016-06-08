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
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;
import com.nextprot.api.annotation.builder.statement.service.RawStatementService;

@Service
public class RawStatementServiceImpl implements RawStatementService {

	@Autowired
	public RawStatementDao rawStatementDao;

	// @Cacheable("modified-entry-annotations")
	public List<ModifiedEntry> getModifiedEntryAnnotation(String entryName) {

		List<ModifiedEntry> modifiedEntries = new ArrayList<ModifiedEntry>();
		List<RawStatement> impactstatements = rawStatementDao.findPhenotypeRawStatements();

		Map<String, List<RawStatement>> impactStatementsByModifiedEntry = impactstatements.stream().collect(Collectors.groupingBy(RawStatement::getBiological_subject_annot_hash));

		impactStatementsByModifiedEntry.keySet().forEach(subjectKey -> {

			// Subject (VD) , variant
			List<RawStatement> subjectVariantStatements = rawStatementDao.findRawStatementsByAnnotHash(subjectKey);
			IsoformAnnotation subjectVariant = buildVariantAnnotation(subjectVariantStatements);

			// Impact annotations
			List<IsoformAnnotation> impactAnnotations = buildAnnotationList(impactStatementsByModifiedEntry.get(subjectKey));

			ModifiedEntry me = new ModifiedEntry();
			me.setSubjectComponents(Arrays.asList(subjectVariant)); // TODO
																	// change
																	// this when
																	// multiple
																	// variants
			me.setAnnotations(impactAnnotations);
			modifiedEntries.add(me);

		});

		return modifiedEntries;

	}

	private static List<IsoformAnnotation> buildAnnotationList(List<RawStatement> impactStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<RawStatement>> impactStatementsByAnnotationHash = impactStatements.stream().collect(Collectors.groupingBy(RawStatement::getAnnot_hash));

		impactStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			IsoformAnnotation isoAnnotation = new IsoformAnnotation();
			List<RawStatement> statements = impactStatementsByAnnotationHash.get(annotationHash);
			if (statements.size() != 1) {
				System.err.println("ups getting " + statements.size() + " statements");
			}

			RawStatement statement = statements.get(0);
			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getAnnotation_category()));
			isoAnnotation.setCategory(category);

			isoAnnotation.setCvTermName(statement.getAnnot_cv_term_name());
			isoAnnotation.setCvTermAccessionCode(statement.getAnnot_cv_term_accession());
			// TODO this should be called terminology I guess! not setCVApiName
			isoAnnotation.setCvApiName(statement.getAnnot_cv_term_terminology());

			isoAnnotation.setAnnotationHash(statement.getAnnot_hash());
			if ((statement.getBiological_object_annot_hash() != null) && (statement.getBiological_object_annot_hash().length() > 0)) {
				BioNormalAnnotation normalAnnotationBioObjectRef = new BioNormalAnnotation();
				normalAnnotationBioObjectRef.setAnnotationHash(statement.getBiological_object_annot_hash());
				isoAnnotation.setBioObject(normalAnnotationBioObjectRef);
			}

			annotations.add(isoAnnotation);

		});
		return annotations;
	}

	private static IsoformAnnotation buildVariantAnnotation(List<RawStatement> subjectVariantStatements) {
		IsoformAnnotation isoAnnotation = new IsoformAnnotation();

		if (subjectVariantStatements.size() != 1) {
			System.err.println("ups getting " + subjectVariantStatements.size() + " variants");
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
		List<RawStatement> normalStatement = rawStatementDao.findNormalRawStatements();
		return buildAnnotationList(normalStatement);
	}

}
