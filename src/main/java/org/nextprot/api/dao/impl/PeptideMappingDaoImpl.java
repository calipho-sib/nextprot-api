package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.dao.PeptideMappingDao;
import org.nextprot.api.domain.IsoformSpecificity;
import org.nextprot.api.domain.PeptideMapping;
import org.nextprot.api.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class PeptideMappingDaoImpl implements PeptideMappingDao {
	
	@Autowired private DataSourceServiceLocator dsLocator;
	
	public List<PeptideMapping> findPeptidesByMasterId(Long id) {
		SqlParameterSource namedParams = new MapSqlParameterSource("id", id);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("peptide-by-master-id"), namedParams, new RowMapper<PeptideMapping>() {

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
	
	public List<PeptideEvidence> findPeptideEvidences(List<String> names) {
		String sql = "select distinct peptide.unique_name, xr.accession, db.cv_name as database_name, ds.cv_name as assigned_by " + 
				"from nextprot.sequence_identifiers peptide, " +
				"nextprot.identifier_resource_assoc ira, " +
				"nextprot.db_xrefs xr, " + 
				"nextprot.cv_databases db, " +
				"nextprot.cv_datasources ds " + 
				"where peptide.identifier_id = ira.identifier_id " + 
				"  and ira.resource_id = xr.resource_id " + 
				"  and xr.cv_database_id = db.cv_id " + 
				"  and ira.datasource_id = ds.cv_id " + 
				"  and ds.cv_name != 'PeptideAtlas' " + 
				"  and peptide.unique_name in (:names)";
		
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParams, new RowMapper<PeptideEvidence>() {

			@Override
			public PeptideEvidence mapRow(ResultSet resultSet, int row) throws SQLException {
				PeptideEvidence evidence = new PeptideEvidence();
				evidence.setPeptideName(resultSet.getString("unique_name"));
				evidence.setAccession(resultSet.getString("accession"));
				evidence.setDatabaseName(resultSet.getString("database_name"));
				evidence.setAssignedBy(resultSet.getString("assigned_by"));
				return evidence;
			}
			
		});
	}

}
