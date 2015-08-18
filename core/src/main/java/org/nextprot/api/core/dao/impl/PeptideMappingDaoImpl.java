package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

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
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("propNameIds", propNameIds);
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-by-master-id"), namedParams, new RowMapper<PeptideMapping>() {

			@Override
			public PeptideMapping mapRow(ResultSet resultSet, int row) throws SQLException {
				PeptideMapping peptideMapping = new PeptideMapping();
				peptideMapping.setPeptideUniqueName(resultSet.getString("pep_unique_name"));
				IsoformSpecificity spec = new IsoformSpecificity(resultSet.getString("iso_unique_name"));
				spec.addPosition(resultSet.getInt("first_pos"), resultSet.getInt("last_pos"));
				peptideMapping.addIsoformSpecificity(spec);
				return peptideMapping;
			}
		});
	}

	
	private List<PeptideEvidence> privateFindPeptideEvidences(List<String> names, boolean withNatural, boolean withSynthetic) {

		String datasourceClause;
		if (withNatural && withSynthetic) 	{ datasourceClause=" true ";  
		} else if (withNatural) 			{ datasourceClause=" ds.cv_name != 'SRMAtlas' ";  			
		} else if (withSynthetic) 			{ datasourceClause=" ds.cv_name  = 'SRMAtlas' ";  
		} else 								{ datasourceClause=" false ";  
		}
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
	public List<PeptideEvidence> findAllPeptideEvidences(List<String> names) {
		return privateFindPeptideEvidences(names,true, true);
	}

	@Override
	public List<PeptideEvidence> findNaturalPeptideEvidences(List<String> names) {
		return privateFindPeptideEvidences(names,true, false);
	}

	@Override
	public List<PeptideEvidence> findSyntheticPeptideEvidences(List<String> names) {
		return privateFindPeptideEvidences(names,false, true);
	}

	@Override
	public List<PeptideMapping> findAllPeptidesByMasterId(Long id) {
		Long[] propNameIds = {52L,53L}; // 52:natural, 53:synthetic 
		return findPeptidesByMasterId(id,Arrays.asList(propNameIds));
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
	public List<Map<String,Object>> findAllPeptideMappingAnnotationsByMasterId(Long id) {
		Long[] propNameIds = {52L,53L}; // 52:natural, 53:synthetic 
		return findPeptideMappingAnnotationsByMasterId(id,Arrays.asList(propNameIds));
	}

	@Override
	public List<Map<String,Object>> findNaturalPeptideMappingAnnotationsByMasterId(Long id) {
		Long[] propNameIds = {52L}; // 52:natural, 53:synthetic 
		return findPeptideMappingAnnotationsByMasterId(id,Arrays.asList(propNameIds));
	}

	@Override
	public List<Map<String,Object>> findSyntheticPeptideMappingAnnotationsByMasterId(Long id) {
		Long[] propNameIds = {53L}; // 52:natural, 53:synthetic 
		return findPeptideMappingAnnotationsByMasterId(id,Arrays.asList(propNameIds));
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
				peptideMapping.put(KEY_ANNOTATION_ID, resultSet.getLong("annotation_id"));
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
	public List<AnnotationProperty> findPeptideAnnotationProperties(List<String> names) {
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("peptide-properties-by-peptide-names"), namedParams, new RowMapper<AnnotationProperty>() {

			@Override
			public AnnotationProperty mapRow(ResultSet resultSet, int row) throws SQLException {
				AnnotationProperty prop = new AnnotationProperty();				
				prop.setAccession(resultSet.getString("peptide_name"));
				prop.setName(resultSet.getString("prop_name"));
				prop.setValue(resultSet.getString("prop_value"));				
				return prop;
			}
			
		});
	}


	@Override
	public List<AnnotationEvidence> findAllPeptideAnnotationEvidences(List<String> names) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<AnnotationEvidence> findNaturalPeptideAnnotationEvidences(List<String> names) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<AnnotationEvidence> findSyntheticPeptideAnnotationEvidences(List<String> names) {
		// TODO Auto-generated method stub
		return null;
	}



	
}
