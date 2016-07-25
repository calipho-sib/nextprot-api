package com.nextprot.api.annotation.builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

abstract class AnnotationBuilder {

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

	protected static List<AnnotationEvidence> buildAnnotationEvidences(List<Statement> Statements) {
		return Statements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setResourceAssociationType("evidence");
			evidence.setQualityQualifier(s.getValue(StatementField.EVIDENCE_QUALITY));

			AnnotationEvidenceProperty evidenceProperty = addPropertyIfPresent(s.getValue(StatementField.EVIDENCE_INTENSITY), "intensity");
			AnnotationEvidenceProperty expContextProperty = addPropertyIfPresent(s.getValue(StatementField.ANNOTATION_SUBJECT_SPECIES), "protein-origin");
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


	protected static void setVariantAttributes(IsoformAnnotation annotation, Statement variantStatement) {

		String original = variantStatement.getValue(StatementField.VARIANT_ORIGINAL_AMINO_ACID);
		String variant = variantStatement.getValue(StatementField.VARIANT_VARIATION_AMINO_ACID);
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);

		String locBegin = variantStatement.getValue(StatementField.LOCATION_BEGIN);
		try {
			Integer positionBeginCanononical = Integer.valueOf(locBegin);
			annotation.setLocationCanonicalBegin(positionBeginCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert begin position " + locBegin);
		}

		String locEnd = variantStatement.getValue(StatementField.LOCATION_END);
		try {
			Integer positionEndCanononical = Integer.valueOf(locEnd);
			annotation.setLocationCanonicalEnd(positionEndCanononical);
		} catch (Exception e) {
			LOGGER.warn("Did not convert end position " + locEnd);
		}

	}
}
