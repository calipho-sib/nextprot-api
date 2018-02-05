package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MdataDao;
import org.nextprot.api.core.domain.Mdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MdataDaoImpl implements MdataDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;
	
	
	@Override
	public List<Mdata> findMdataForNextprotPTMs(List<Long> evidenceIdList) {
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", evidenceIdList);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("nextprot-ptm-mdata-by-evidence-ids"), 
				namedParameters, 
				new MdataRowMapper());
	}
	
	
	@Override
	public List<Mdata> findMdataForNonNextprotPTMs(List<Long> evidenceIdList) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", evidenceIdList);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("phosphoproteome-ptm-mdata-by-evidence-ids"), 
				namedParameters, 
				new MdataRowMapper());
	}
	

	
	
	private static class MdataRowMapper implements ParameterizedRowMapper<Mdata> {

		@Override
		public Mdata mapRow(ResultSet rs, int row) throws SQLException {
			
			Mdata mdata = new Mdata();
			mdata.setEvidenceId(rs.getLong("evidence_id"));
			mdata.setId(rs.getLong("mdata_id"));
			mdata.setAccession(rs.getString("mdata_ac"));
			mdata.setTitle(rs.getString("mdata_title"));
			mdata.setRawXml(rs.getString("mdata_xml"));
			return mdata;
		}
	}



	//  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// methods that are useful for integration tests and "data model" documentation
	//  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@Override
	public List<Long> findExamplesOfEvidencesHavingMdataForNextprotPTMs(int sampleSize) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("sample_size", sampleSize);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("nextprot-ptm-evidence-id-examples"), 
				namedParameters, 
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int arg1) throws SQLException {
						return rs.getLong("evidence_id");
					}});
	}


	@Override
	public List<Long> findExamplesOfEvidencesHavingMdataForNonNextprotPTMs(int sampleSize) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("sample_size", sampleSize);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("phosphoproteome-ptm-evidence-id-examples"), 
				namedParameters, 
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int arg1) throws SQLException {
						return rs.getLong("evidence_id");
					}});
	};

}
