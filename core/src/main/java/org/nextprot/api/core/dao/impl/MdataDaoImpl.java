package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map<Long, Long> findEvidenceIdMdataIdMapForPTMsByEntryName(String ac) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("entry_name", ac);
		Map<Long,Long> map = new HashMap<>();
		EvidenceMdataMapRowMapper mapper = new EvidenceMdataMapRowMapper(map);
		new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("ptm-evidence-with-mdata-by-unique-name"), 
				namedParameters, 
				mapper);
		return map;		
	};

	@Override
	public List<Mdata> findMdataByIds(List<Long> mdataIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("mdata_ids", mdataIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("mdata-by-ids"), 
				namedParameters, 
				new MdataRowMapper());	}	
	
	
	
	private static class MdataRowMapper implements ParameterizedRowMapper<Mdata> {

		@Override
		public Mdata mapRow(ResultSet rs, int row) throws SQLException {
			
			Mdata mdata = new Mdata();
			mdata.setId(rs.getLong("mdata_id"));
			mdata.setAccession(rs.getString("mdata_ac"));
			mdata.setTitle(rs.getString("mdata_title"));
			mdata.setRawXml(rs.getString("mdata_xml"));
			return mdata;
		}
	}

	private static class EvidenceMdataMapRowMapper implements ParameterizedRowMapper<Object> {

		private Map<Long,Long> map;
		public EvidenceMdataMapRowMapper(Map<Long,Long> map) {
			this.map=map;
		}
		
		@Override
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			map.put(rs.getLong("evidence_id"), rs.getLong("mdata_id"));
			return null;
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
	}









}
