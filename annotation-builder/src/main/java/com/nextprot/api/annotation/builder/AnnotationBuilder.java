package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioGenericObject;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementField;

public class AnnotationBuilder {

	private static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class);

	private static AnnotationEvidenceProperty addPropertyIfPresent(String propertyValue, String propertyName) {
		if (propertyValue != null) {
			AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
			prop.setPropertyName(propertyName);
			prop.setPropertyValue(propertyValue);
			return prop;
		}
		return null;
	}

	private static List<AnnotationEvidence> buildAnnotationEvidences(List<RawStatement> rawStatements) {
		return rawStatements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setResourceAssociationType("evidence");
			evidence.setQualityQualifier(s.getValue(StatementField.STATEMENT_QUALITY));

			AnnotationEvidenceProperty evidenceProperty = addPropertyIfPresent(s.getValue(StatementField.EXP_CONTEXT_PROPERTY_INTENSITY), "intensity");
			AnnotationEvidenceProperty expContextProperty = addPropertyIfPresent(s.getValue(StatementField.EXP_CTX_PRPTY_PROTEIN_ORIGIN), "protein-origin");
			AnnotationEvidenceProperty sourceAccession =addPropertyIfPresent(s.getValue(StatementField.ANNOT_SOURCE_ACCESSION), "source-accession");

			//Set properties which are not null
			evidence.setProperties(
					Arrays.asList(evidenceProperty, expContextProperty, sourceAccession)
						.stream().filter(p -> p != null)
						.collect(Collectors.toList())
						);

			return evidence;
		}).collect(Collectors.toList());

	}

	public static IsoformAnnotation buildAnnotation(String isoformName, List<RawStatement> flatStatements) {
		List<IsoformAnnotation> annotations = buildAnnotationList(isoformName, flatStatements);
		if(annotations.isEmpty() || annotations.size() > 1){
			throw new NextProtException("Expecting 1 annotation but found " + annotations.size() + " from " + flatStatements.size());
		}
		return annotations.get(0);
	}

	public static List<IsoformAnnotation> buildAnnotationList(String isoformName, List<RawStatement> flatStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<RawStatement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(RawStatement::getIsoformAnnotationId));

		flatStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			IsoformAnnotation isoAnnotation = new IsoformAnnotation();
			List<RawStatement> statements = flatStatementsByAnnotationHash.get(annotationHash);

			isoAnnotation.setEvidences(buildAnnotationEvidences(statements));

			RawStatement statement = statements.get(0);

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getValue(StatementField.ANNOTATION_CATEGORY)));
			isoAnnotation.setCategory(category);

			if (category.equals(AnnotationCategory.VARIANT))
				setVariantAttributes(isoAnnotation, statement);

			isoAnnotation.setIsoformName(isoformName);
			isoAnnotation.setCvTermName(statement.getValue(StatementField.ANNOT_CV_TERM_NAME));

			isoAnnotation.setDescription(statement.getValue(StatementField.ANNOT_DESCRIPTION));
			isoAnnotation.setCvTermAccessionCode(statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION));
			// TODO this should be called terminology I guess! not setCVApiName
			isoAnnotation.setCvApiName(statement.getValue(StatementField.ANNOT_CV_TERM_TERMINOLOGY));

			isoAnnotation.setAnnotationHash(statement.getIsoformAnnotationId());
			isoAnnotation.setAnnotationUniqueName(statement.getValue(StatementField.ANNOT_ISO_UNAME));
			
			String boah = statement.getValue(StatementField.OBJECT_ANNOT_ISO_IDS);
			String boa = statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
			String bot = statement.getValue(StatementField.BIOLOGICAL_OBJECT_TYPE);

			if ((boah != null) && (boah.length() > 0) || (boa != null && (boa.length() > 0))) {

				BioGenericObject bioObject = new BioGenericObject();
				bioObject.setAccession(boa); // In case of interactions
				bioObject.setType(bot);
				bioObject.setAnnotationHash(boah); // In case of phenotypes
				isoAnnotation.setBioObject(bioObject);

			}

			annotations.add(isoAnnotation);

		});
		return annotations;
	}
	
	
	private static void setVariantAttributes(IsoformAnnotation annotation, RawStatement variantStatement) {

		String original = variantStatement.getValue(StatementField.VARIANT_ORIGINAL_AMINO_ACID);
		String variant = variantStatement.getValue(StatementField.VARIANT_VARIATION_AMINO_ACID);
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);

		String locBegin = variantStatement.getValue(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF);
		try {
			Integer positionBeginCanononical = Integer.valueOf(locBegin);
			annotation.setLocationCanonicalBegin(positionBeginCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert begin position " + locBegin);
		}

		String locEnd = variantStatement.getValue(StatementField.ANNOT_LOC_END_CANONICAL_REF);
		try {
			Integer positionEndCanononical = Integer.valueOf(locEnd);
			annotation.setLocationCanonicalEnd(positionEndCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert end position " + locEnd);
		}

	}
}
