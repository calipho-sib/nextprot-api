package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.dao.KeywordDao;
import org.nextprot.api.domain.Keyword;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class KeywordDaoImpl implements KeywordDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Keyword> findKeywordByMaster(String uniqueName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqueName", uniqueName);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("keywords-by-entry-name"), params, new KeywordRowMapper());
	}
	
	private static class KeywordRowMapper implements ParameterizedRowMapper<Keyword> {

		@Override
		public Keyword mapRow(ResultSet resultSet, int row) throws SQLException {
			Keyword kw = new Keyword();
			kw.setAccession(resultSet.getString("accession"));
			kw.setName(resultSet.getString("keyword_name"));
			return kw;
		}
		
	}

}
