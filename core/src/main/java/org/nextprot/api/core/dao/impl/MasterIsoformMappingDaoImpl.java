package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MasterIsoformMappingDaoImpl implements MasterIsoformMappingDao {
	
	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private DataSourceServiceLocator dsLocator;
	
	
	@Override
	public List<IsoformSpecificity> findIsoformMappingByMaster(String ac) {
		SqlParameterSource namedParams = new MapSqlParameterSource("entryName", ac);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("master-isoform-mapping-by-entry-name"), namedParams, new RowMapper<IsoformSpecificity>() {

			@Override
			public IsoformSpecificity mapRow(ResultSet resultSet, int row) throws SQLException {
				IsoformSpecificity spec = new IsoformSpecificity(null, resultSet.getString("isoform_ac"));
				spec.addPosition(Pair.create(resultSet.getInt("first_pos"), resultSet.getInt("last_pos")));
				return spec;
			}			
		});
	}

	@Override
	public Map<String,List<NucleotidePositionRange>> findMasterIsoformMapping(String ac) {
		
		SqlParameterSource namedParams = new MapSqlParameterSource("entryName", ac);
		
		List<IsoMasterNuRangePos> isoNuRanges = new NamedParameterJdbcTemplate( dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("master-isoform-mapping-by-entry-name"), namedParams, new RowMapper<IsoMasterNuRangePos>() {

			@Override
			public IsoMasterNuRangePos mapRow(ResultSet resultSet, int row) throws SQLException {
				IsoMasterNuRangePos rec = new IsoMasterNuRangePos();
				rec.iso = resultSet.getString("isoform_ac");
				rec.nuPosRange = new NucleotidePositionRange(resultSet.getInt("first_pos"), resultSet.getInt("last_pos"));
				return rec;
			}			
		});
		
		Map<String,List<NucleotidePositionRange>> mapIsoNuRanges =  new HashMap<String,List<NucleotidePositionRange>>();
		for (IsoMasterNuRangePos rec: isoNuRanges) {
			if (!mapIsoNuRanges.containsKey(rec.iso)) mapIsoNuRanges.put(rec.iso, new ArrayList<NucleotidePositionRange>());
			mapIsoNuRanges.get(rec.iso).add(new NucleotidePositionRange(rec.nuPosRange.getLower(), rec.nuPosRange.getUpper()));
		}
		return mapIsoNuRanges;
	}

	private class IsoMasterNuRangePos {
		public String iso;
		public NucleotidePositionRange nuPosRange;
	}
	
}
