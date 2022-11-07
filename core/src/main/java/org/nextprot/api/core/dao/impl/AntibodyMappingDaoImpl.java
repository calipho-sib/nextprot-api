package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.AnnotationMapping2Annotation;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class AntibodyMappingDaoImpl implements AntibodyMappingDao {

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public List<Annotation> findAntibodyMappingAnnotationsById(long masterId) {
		
		SqlParameterSource namedParams = new MapSqlParameterSource("id", masterId);

		AntibodyRowMapper rowMapper =  new AntibodyRowMapper(); 
		new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("antibodies-by-id"), namedParams, rowMapper);
		return rowMapper.getAnnotations();
	}

	@Override
	public List<String> findAntibodyIsoformMappingsList() {
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("antibody-isoform-mappings"),new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int row) throws SQLException {
				return rs.getString("ab_unique_name") + ":" + rs.getString("iso_names");
			}
		});
	}

	private static class AntibodyRowMapper implements RowMapper<Annotation>{
		
		Map<Long, Annotation> annotationsMap = new HashMap<>();
		
		@Override
		public Annotation mapRow(ResultSet resultSet, int row) throws SQLException {

			Long tmpId = resultSet.getLong("annotation_id");
			Long annotId = tmpId + IdentifierOffset.ANTIBODY_MAPPING_ANNOTATION_OFFSET;
			Long evidenceId = tmpId + IdentifierOffset.ANTIBODY_MAPPING_ANNOTATION_EVIDENCE_OFFSET;
			String annotUniqueName = "AN_"+resultSet.getString("resource_ac")+"_"+annotId; // AN_HPA039796_000890
			final AnnotationMapping2Annotation amam = AnnotationMapping2Annotation.ANTIBODY_MAPPING;
					
            String isoName = resultSet.getString("iso_unique_name");

			if (!annotationsMap.containsKey(annotId)){

                Annotation annotation = new Annotation();
                
                annotation.setAnnotationId(annotId);
                annotation.setUniqueName(annotUniqueName); 
				annotation.setAnnotationCategory(AnnotationCategory.ANTIBODY_MAPPING);
                annotation.setQualityQualifier("GOLD"); // TODO: IS THIS KIND OF INFO ACCESSIBLE ?
				annotation.addTargetingIsoforms(new ArrayList<AnnotationIsoformSpecificity>());

				// Add property "antibody name"
				List<AnnotationProperty> props = new ArrayList<>();
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annotation.getAnnotationId());
				prop.setName(PropertyApiModel.NAME_ANTIBODY_NAME);
				prop.setValue(resultSet.getString("antibody_unique_name"));
				props.add(prop);
				annotation.addProperties(props);

				AnnotationEvidence evidence = new AnnotationEvidence();
                evidence.setAnnotationId(annotId);
                evidence.setNegativeEvidence(false);
				evidence.setAssignmentMethod(amam.getAssignmentMethod());
				evidence.setEvidenceCodeAC(amam.getEcoAC());
				evidence.setEvidenceCodeOntology(amam.getEcoOntology());
				evidence.setQualityQualifier("GOLD");
				evidence.setEvidenceCodeName(amam.getEcoName());
                evidence.setResourceAssociationType("evidence");
                evidence.setResourceAccession(resultSet.getString("resource_ac"));
                evidence.setResourceType("database");
                evidence.setResourceId(resultSet.getLong("db_xref_id"));
                evidence.setAssignedBy(resultSet.getString("antibody_src"));
                evidence.setResourceDb(resultSet.getString("resource_db"));
                evidence.setEvidenceId(evidenceId);
                annotation.setEvidences(Collections.singletonList(evidence));

                annotationsMap.put(annotId, annotation);
			}

			Annotation annotation = annotationsMap.get(annotId);

			AnnotationIsoformSpecificity isoSpecificity = new AnnotationIsoformSpecificity();

            isoSpecificity.setIsoformAccession(isoName);
			isoSpecificity.setFirstPosition((Integer)resultSet.getObject("first_pos")); // better than using getInt which returns a primitive and can not be null
			isoSpecificity.setLastPosition((Integer)resultSet.getObject("last_pos"));
            isoSpecificity.setSpecificity("SPECIFIC");

			annotation.getTargetingIsoformsMap().put(isoName, isoSpecificity);

			return annotation;
		}
		
		public List<Annotation> getAnnotations() {
			return new ArrayList<>(annotationsMap.values());
		}
	}
}
