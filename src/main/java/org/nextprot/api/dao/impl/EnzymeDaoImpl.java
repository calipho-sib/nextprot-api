package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.dao.EnzymeDao;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.nextprot.rdf.domain.Terminology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class EnzymeDaoImpl implements EnzymeDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Terminology> findEnzymeByMaster(String uniqueName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqueName", uniqueName);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("enzyme-by-entry-name"), params, new EnzymeRowMapper());
	}
	
	private static class EnzymeRowMapper implements ParameterizedRowMapper<Terminology> {

		@Override
		public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {
			Terminology kw = new Terminology();
			kw.setAccession(resultSet.getString("accession"));
			kw.setName(resultSet.getString("name"));
			return kw;
		}
		
	}

}
