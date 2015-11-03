package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EnzymeDao;
import org.nextprot.api.core.domain.Terminology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EnzymeDaoImpl implements EnzymeDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Terminology> findEnzymeByMaster(String uniqueName) {

		Map<String, Object> params = new HashMap<>();
		params.put("uniqueName", uniqueName);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("enzyme-by-entry-name"), params, new EnzymeRowMapper());
	}
	
	private static class EnzymeRowMapper implements ParameterizedRowMapper<Terminology> {

		@Override
		public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {

			Terminology kw = new Terminology();

			kw.setOntology(resultSet.getString("ontology"));
			kw.setAccession(resultSet.getString("accession"));
			kw.setName(resultSet.getString("name"));

			return kw;
		}
		
	}

}
