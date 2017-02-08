package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.AnnotationMapping2Annotation;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
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
public class PeptideMappingDaoImpl implements PeptideMappingDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	
	/* 
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 * old implementation Peptide* domain object based (soon obsoleted)
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 * 
	 */
	
	private List<PeptideMapping> findPeptidesByMasterId(Long id, List<Long> propNameIds) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("propNameIds", propNameIds);
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-by-master-id"), namedParams, new RowMapper<PeptideMapping>() {

			@Override
			public PeptideMapping mapRow(ResultSet resultSet, int row) throws SQLException {
				PeptideMapping peptideMapping = new PeptideMapping();
				peptideMapping.setPeptideUniqueName(resultSet.getString("pep_unique_name"));
				AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
				spec.setIsoformAccession(resultSet.getString("iso_unique_name"));
				spec.setFirstPosition(resultSet.getInt("first_pos"));
				spec.setLastPosition(resultSet.getInt("last_pos"));
				peptideMapping.addIsoformSpecificity(spec);
				return peptideMapping;
			}
		});
	}

	
	private List<PeptideEvidence> findPeptideEvidences(List<String> names, boolean natural) {

		String datasourceClause = natural ? " ds.cv_name != 'SRMAtlas' " : " ds.cv_name  = 'SRMAtlas' ";  
		String sql = sqlDictionary.getSQLQuery("peptide-evidences-by-peptide-names").replace(":datasourceClause", datasourceClause);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("names", names);
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParams, new RowMapper<PeptideEvidence>() {
			@Override
			public PeptideEvidence mapRow(ResultSet resultSet, int row) throws SQLException {
				PeptideEvidence evidence = new PeptideEvidence();
				evidence.setPeptideName(resultSet.getString("unique_name"));
				evidence.setAccession(resultSet.getString("accession"));
				evidence.setDatabaseName(resultSet.getString("database_name"));
				evidence.setAssignedBy(resultSet.getString("assigned_by"));
				evidence.setResourceId(resultSet.getLong("resource_id"));
				evidence.setResourceType(resultSet.getString("resource_type"));
				return evidence;
			}
		});
	}
	
	@Override
	public List<PeptideProperty> findPeptideProperties(List<String> names) {
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-properties-by-peptide-names"), namedParams, new RowMapper<PeptideProperty>() {

			@Override
			public PeptideProperty mapRow(ResultSet resultSet, int row) throws SQLException {
				PeptideProperty prop = new PeptideProperty();
				prop.setPeptideId(resultSet.getLong("peptide_id"));
				prop.setPeptideName(resultSet.getString("peptide_name"));
				prop.setId(resultSet.getLong("property_id"));
				prop.setNameId(resultSet.getLong("prop_name_id"));
				prop.setName(resultSet.getString("prop_name"));
				prop.setValue(resultSet.getString("prop_value"));
				return prop;
			}
			
		});
	}
	
	
	@Override
	public List<PeptideEvidence> findNaturalPeptideEvidences(List<String> names) {
		return findPeptideEvidences(names, true);
	}

	@Override
	public List<PeptideEvidence> findSyntheticPeptideEvidences(List<String> names) {
		return findPeptideEvidences(names, false);
	}

	@Override
	public List<PeptideMapping> findNaturalPeptidesByMasterId(Long id) {
		Long[] propNameIds = {52L}; // 52:natural, 53:synthetic 
		return findPeptidesByMasterId(id,Arrays.asList(propNameIds));
	}

	@Override
	public List<PeptideMapping> findSyntheticPeptidesByMasterId(Long id) {
		Long[] propNameIds = {53L}; // 52:natural, 53:synthetic 
		return findPeptidesByMasterId(id,Arrays.asList(propNameIds));
	}


	/* 
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 * new implementation Annotation* domain object based
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 * 
	 */
	
	@Override
	public List<Map<String,Object>> findPeptideMappingAnnotationsByMasterId(Long id, boolean withNatural, boolean withSynthetic) {
		// 52:natural, 53:synthetic 
		List<Long> propNameIdList = new ArrayList<>();
		if (withNatural) propNameIdList.add(52L);
		if (withSynthetic) propNameIdList.add(53L);
		return findPeptideMappingAnnotationsByMasterId(id,propNameIdList);
	}
	
	
	private List<Map<String,Object>> findPeptideMappingAnnotationsByMasterId(Long id, List<Long> propNameIds) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("propNameIds", propNameIds);
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-annotation-by-master-id"), namedParams, new RowMapper<Map<String,Object>>() {
			@Override
			public Map<String,Object> mapRow(ResultSet resultSet, int row) throws SQLException {
				Map<String,Object> peptideMapping = new HashMap<>();
				
				peptideMapping.put(KEY_ANNOTATION_ID, resultSet.getLong("annotation_id") + IdentifierOffset.PEPTIDE_MAPPING_ANNOTATION_OFFSET );
				peptideMapping.put(KEY_QUALITY_QUALIFIER,resultSet.getString("quality_qualifier"));
				peptideMapping.put(KEY_PEP_UNIQUE_NAME,resultSet.getString("pep_unique_name"));
				peptideMapping.put(KEY_ISO_UNIQUE_NAME,resultSet.getString("iso_unique_name"));
				peptideMapping.put(KEY_RANK,resultSet.getInt("rank"));
				peptideMapping.put(KEY_FIRST_POS,resultSet.getInt("first_pos"));
				peptideMapping.put(KEY_LAST_POS,resultSet.getInt("last_pos"));
				return peptideMapping;
			}
		});
	}

	
	@Override
	public Map<String,List<AnnotationProperty>> findPeptideAnnotationPropertiesMap(List<String> names) {
		
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		List<AnnotationProperty> props = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-properties-by-peptide-names"), namedParams, new RowMapper<AnnotationProperty>() {
			@Override
			public AnnotationProperty mapRow(ResultSet resultSet, int row) throws SQLException {
				AnnotationProperty prop = new AnnotationProperty();				
				prop.setAccession(resultSet.getString("peptide_name"));
				prop.setName(resultSet.getString("prop_name"));
				prop.setValue(resultSet.getString("prop_value"));		
				return prop;
			}
		});
		Map<String,List<AnnotationProperty>> result = new HashMap<>();
		for (AnnotationProperty p: props) {
			String pepKey = p.getAccession();
			if (!result.containsKey(pepKey)) result.put(pepKey, new ArrayList<AnnotationProperty>());
			p.setAccession(null);
			result.get(pepKey).add(p);
		}
		return result;
	}


	@Override
	public Map<String,List<AnnotationEvidence>> findPeptideAnnotationEvidencesMap(List<String> names, boolean natural) {

		String datasourceClause = natural ? " ds.cv_name != 'SRMAtlas'" : " ds.cv_name  = 'SRMAtlas'";  
		final AnnotationMapping2Annotation pmam = natural ? AnnotationMapping2Annotation.PEPTIDE_MAPPING : AnnotationMapping2Annotation.SRM_PEPTIDE_MAPPING;
		String sql = sqlDictionary.getSQLQuery("peptide-evidences-by-peptide-names").replace(":datasourceClause", datasourceClause);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("names", names);
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		List<AnnotationEvidence> evidences = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParams, new RowMapper<AnnotationEvidence>() {
			@Override
			public AnnotationEvidence mapRow(ResultSet resultSet, int row) throws SQLException {
				AnnotationEvidence evidence = new AnnotationEvidence();
				evidence.setAnnotationId(0); // set later by service
				evidence.setEvidenceId(resultSet.getLong("evidence_id") +  IdentifierOffset.PEPTIDE_MAPPING_ANNOTATION_EVIDENCE_OFFSET);
				evidence.setAssignedBy(resultSet.getString("assigned_by"));
				evidence.setAssignmentMethod(pmam.getAssignmentMethod());
				evidence.setEvidenceCodeAC(pmam.getEcoAC());
				evidence.setEvidenceCodeOntology(pmam.getEcoOntology());
				evidence.setEvidenceCodeName(pmam.getEcoName());
				evidence.setExperimentalContextId(null);
				evidence.setNegativeEvidence(false);
				evidence.setProperties(new ArrayList<AnnotationEvidenceProperty>());
				evidence.setQualifierType(pmam.getQualifierType());
				evidence.setQualityQualifier(pmam.getQualityQualifier());
				evidence.setResourceAccession(resultSet.getString("accession"));
				evidence.setResourceAssociationType("evidence");
				evidence.setResourceDb(resultSet.getString("database_name"));
				evidence.setResourceDescription(resultSet.getString("unique_name")); // temp value
				evidence.setResourceId(resultSet.getLong("resource_id"));
				evidence.setResourceType(resultSet.getString("resource_type"));
				return evidence;
			}
		});
		// turn this list into a map having the peptide name as the key
		Map<String,List<AnnotationEvidence>> result = new HashMap<>();
		for (AnnotationEvidence ev: evidences) {
			String pepKey = ev.getResourceDescription();
			ev.setResourceDescription(null); // remove temp value
			if (!result.containsKey(pepKey)) result.put(pepKey, new ArrayList<AnnotationEvidence>());
			result.get(pepKey).add(ev);
		}
		return result;
	}

	
}
