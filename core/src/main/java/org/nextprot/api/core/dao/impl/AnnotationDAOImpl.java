package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariationHGVSFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.impl.spring.BatchNamedParameterJdbcTemplate;
import org.nextprot.api.core.domain.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;


@Component
public class AnnotationDAOImpl implements AnnotationDAO {

	private static SequenceVariationHGVSFormat MUTATION_HGV_FORMAT = new SequenceVariationHGVSFormat();

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;
	
	private static class AnnotationRowMapper implements ParameterizedRowMapper<Annotation> {

		public Annotation mapRow(ResultSet resultSet, int row) throws SQLException {

			Annotation annotation = new Annotation();
			
			String category = adaptAnnotationCategoryToApiDataModel(resultSet);
			
			annotation.setAnnotationId(resultSet.getLong("annotation_id"));
			annotation.setCategory(category);
			annotation.setDescription(resultSet.getString("description"));
			annotation.setQualityQualifier(resultSet.getString("quality_qualifier"));
			annotation.setCvTermName(resultSet.getString("cv_term_name"));
			annotation.setCvTermAccessionCode(resultSet.getString("cv_term_accession"));
			annotation.setCvTermType(resultSet.getString("cv_term_type"));
			annotation.setCvTermDescription(resultSet.getString("cv_term_description"));
			annotation.setCvApiName(resultSet.getString("cv_api_name"));
			annotation.setSynonym(resultSet.getString("synonym"));
			if (resultSet.getString("synonyms") != null) annotation.setSynonyms(Arrays.asList(resultSet.getString("synonyms").split("\\|")));
			annotation.setUniqueName(resultSet.getString("annotation_unique_name"));

			// Set the variant if it exists
			if ((resultSet.getString("original_sequence") != null) || (resultSet.getString("variant_sequence") != null)) {
				annotation.setVariant(new AnnotationVariant(
						resultSet.getString("original_sequence"), 
						resultSet.getString("variant_sequence"), 
						annotation.getDescription()));
			}

			
			return annotation;
		}

		/*
		 * Small changes on the fly to be compliant with API data model:
         * - dbId=1050 "biotechnology": move all annotations to existing type dbId=1052 "Miscellaneous"
         * - dbId=1005 "transmembrane region": move annotations to new type "intramembrane region" if annotation.cv_term_id=51748 "In membrane"
		 * - dbId=1002 "transit peptide": split annotations into 2 new types "mitochondrial transit peptide" and "peroxysome transit peptide"
		 */
		public String adaptAnnotationCategoryToApiDataModel(ResultSet rs) throws SQLException {
			
			String category = rs.getString("category");			
			
    		if (category.equals("biotechnology")) {
				category= AnnotationCategory.MISCELLANEOUS.getDbAnnotationTypeName();
				
			} else if (category.equals("transmembrane region")) {
				int termId = rs.getInt("cv_term_id");
				if (termId==51748) category = AnnotationCategory.INTRAMEMBRANE_REGION.getDbAnnotationTypeName();
				
			} else if (category.equals("transit peptide")) {
				int termId = rs.getInt("cv_term_id");
				if (termId==51743) {
					category = AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE.getDbAnnotationTypeName();
					//System.out.println("transit peptide => mito");
				} else if (termId==51744) {
					category = AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE.getDbAnnotationTypeName();
					//System.out.println("transit peptide => perox");
				} else {
					//System.out.println("transit peptide => ???");					
				}
			}
			return category;
		}
		
	}


	
	public List<Annotation> findAnnotationsByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("annotations-by-entry-name"), namedParameters, new AnnotationRowMapper());
	}

	// Annotation Isoforms /////////////////////////////////////////////////////////////////////////////

	private static class AnnotationIsoformRowMapper implements ParameterizedRowMapper<AnnotationIsoformSpecificity> {

		public AnnotationIsoformSpecificity mapRow(ResultSet resultSet, int row) throws SQLException {

			AnnotationIsoformSpecificity annotation = new AnnotationIsoformSpecificity();
			annotation.setAnnotationId(resultSet.getLong("annotation_id"));
			annotation.setFirstPosition((Integer)resultSet.getObject("first_pos"));  // the SQL increments first_pos by 1
			annotation.setLastPosition((Integer)resultSet.getObject("last_pos"));
			annotation.setIsoformName(resultSet.getString("unique_name"));
			annotation.setSpecificity(resultSet.getString("iso_specificity"));
			return annotation;
		}
	};

	public List<AnnotationIsoformSpecificity> findAnnotationIsoformsByAnnotationIds(List<Long> annotationIds) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", annotationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("annotation-isoforms-by-annotation-ids"), namedParameters, new AnnotationIsoformRowMapper());

	}

	// Annotation Evidences /////////////////////////////////////////////////////////////////////////////

	@Override
	public List<AnnotationEvidence> findAnnotationEvidencesByAnnotationIds(List<Long> annotationIds) {

		return new BatchNamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("annotation-evidences-by-annotation-ids"), "ids", annotationIds, new ParameterizedRowMapper<AnnotationEvidence>() {

			public AnnotationEvidence mapRow(ResultSet resultSet, int row) throws SQLException {
					
				AnnotationEvidence evidence = new AnnotationEvidence();
				evidence.setEvidenceCodeOntology(resultSet.getString("ontology"));
				evidence.setNegativeEvidence(resultSet.getBoolean("is_negative_evidence"));
				evidence.setAnnotationId(resultSet.getLong("annotation_id"));
				evidence.setResourceId(resultSet.getLong("resource_id"));
				evidence.setQualifierType(resultSet.getString("qualifier_type"));
				evidence.setResourceAssociationType(resultSet.getString("resource_assoc_type"));
				evidence.setResourceType(resultSet.getString("resource_type"));
				evidence.setResourceAccession(resultSet.getString("resource_accession"));
				evidence.setResourceDb(resultSet.getString("resource_db"));
				evidence.setResourceDescription(resultSet.getString("resource_desc"));
				evidence.setPublicationMD5(resultSet.getString("publication_md5"));
				evidence.setQualityQualifier(resultSet.getString("quality_qualifier"));
				evidence.setEvidenceId(resultSet.getLong("evidence_id"));
				evidence.setAssignedBy(resultSet.getString("evidence_assigned_by"));
				evidence.setExperimentalContextId(resultSet.getLong("experimental_context_id"));
				evidence.setAssignmentMethod(resultSet.getString("assignment_method"));
				evidence.setEvidenceCodeAC(resultSet.getString("eco_ac"));
				evidence.setEvidenceCodeName(resultSet.getString("eco_name"));
				return evidence;
			}
		});

	}
	
	
	
	@Override
	public List<AnnotationEvidenceProperty> findAnnotationEvidencePropertiesByEvidenceIds(List<Long> evidenceIds) {
		
		return new BatchNamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("annotation-evidence-properties-by-evidence-ids"), "ids", evidenceIds, new ParameterizedRowMapper<AnnotationEvidenceProperty>() {

			public AnnotationEvidenceProperty mapRow(ResultSet resultSet, int row) throws SQLException {

				AnnotationEvidenceProperty evidenceProperty = new AnnotationEvidenceProperty();
				evidenceProperty.setEvidenceId(resultSet.getLong("evidence_id"));
				//String pname = resultSet.getString("property_name");
				evidenceProperty.setPropertyName(resultSet.getString("property_name"));
				evidenceProperty.setPropertyValue(resultSet.getString("property_value"));
				return evidenceProperty;
				
			}
		});

	}


	
	// Annotation Properties /////////////////////////////////////////////////////////////////////////////

	@Override
	public List<AnnotationProperty> findAnnotationPropertiesByAnnotationIds(List<Long> annotationIds) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", annotationIds);

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("annotation-properties-by-annotation-ids"), namedParameters, new ParameterizedRowMapper<AnnotationProperty>() {

			public AnnotationProperty mapRow(ResultSet resultSet, int row) throws SQLException {
			
				AnnotationProperty property = new AnnotationProperty();

				property.setAnnotationId(resultSet.getLong("annotation_id"));
				property.setAccession(resultSet.getString("accession"));
				property.setName(resultSet.getString("property_name"));
				property.setValue(resultSet.getString("property_value"));
				// quick fix to prevent errors in generating hgvs format because we dont need it for the moment (property will be hidden)
				//setPropertyNameValue(property, resultSet.getString("property_name"), resultSet.getString("property_value"));

				return property;
			}
		});
	}

	static void setPropertyNameValue(AnnotationProperty property, String name, String value) {

		property.setName(name);
		try {
			property.setValue((name.equals("mutation AA")) ?
					// TODO: 'mutation AA' property comes from COSMIC. Some values could be not corrected formatter according to the last version v2.0 of HGV
					// This reformatting should be done at NP integration time, even better, this should be done by COSMIC guys !
					MUTATION_HGV_FORMAT.format(MUTATION_HGV_FORMAT.parse(value, SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE), AminoAcidCode.CodeType.THREE_LETTER)
					: value);
		} catch (ParseException e) {
			throw new NextProtException(e);
		}
	}
}
