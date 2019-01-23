package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ProteinExistenceDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ProteinExistenceDaoImpl implements ProteinExistenceDao {

	@Autowired	private SQLDictionary sqlDictionary;
	@Autowired	private DataSourceServiceLocator dsLocator;
	
	@Override
	public ProteinExistence findProteinExistenceUniprot(String uniqueName, ProteinExistence.Source source) {
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dsLocator.getDataSource());

		if (source == ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT) {
			return template.queryForObject(sqlDictionary.getSQLQuery("entry-pe-uniprot"), namedParameters, new ProteinExistenceRowMapper());
		}
		else if (source == ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT1) {
			return template.queryForObject(sqlDictionary.getSQLQuery("entry-pe-np1"), namedParameters, new ProteinExistenceRowMapper());
		}
		return null;
	}

	private static class ProteinExistenceRowMapper extends SingleColumnRowMapper<ProteinExistence> {

		@Override
		public ProteinExistence mapRow(ResultSet resultSet, int row) throws SQLException {

			return ProteinExistence.valueOfKey(resultSet.getString("pe"));
		}
	}
}
