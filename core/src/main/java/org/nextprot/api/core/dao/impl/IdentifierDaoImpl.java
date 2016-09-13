package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.IdentifierDao;
import org.nextprot.api.core.domain.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class IdentifierDaoImpl implements IdentifierDao {

	private static final Map<String, String> DB_TYPE_NP1_NAMES;

	static {
		DB_TYPE_NP1_NAMES = new HashMap<>();

		// See https://issues.isb-sib.ch/browse/CALIPHOMISC-359
		DB_TYPE_NP1_NAMES.put("UNIPROT", "UniProtKB");
		DB_TYPE_NP1_NAMES.put("CLONE_NAME", "CLONE NAMES");
		DB_TYPE_NP1_NAMES.put("MICROARRAY_PROBE", "MICROARRAY PROBE IDENTIFIERS");
		DB_TYPE_NP1_NAMES.put("NCBI", "NCBI");
		DB_TYPE_NP1_NAMES.put("ACCESSION_CODE", "ACCESSION CODES");
		DB_TYPE_NP1_NAMES.put("ADDITIONAL_IDENTIFIER", "ADDITIONAL IDENTIFIERS");
		DB_TYPE_NP1_NAMES.put("ENSEMBL", "ENSEMBL");
	}

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;

	@Override
	public List<Identifier> findIdentifiersByMaster(String uniqueName) {
		Map<String, Object> params = new HashMap<>();
		params.put("uniqueName", uniqueName);

		List<Identifier> ids = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("identifiers-by-master-unique-name"), params, new IdentifierRowMapper());

		// See CALIPHOMISC-489
		return ids.stream()
				.filter(Objects::nonNull)
				.filter(i -> !("Ensembl".equals(i.getDatabase()) && !i.getName().startsWith("ENSG")))
				.collect(Collectors.toList());
	}
	
	private static class IdentifierRowMapper implements ParameterizedRowMapper<Identifier> {

		@Override
		public Identifier mapRow(ResultSet resultSet, int row) throws SQLException {
			Identifier identifier = new Identifier();
			identifier.setName(resultSet.getString("identifier_name"));
			identifier.setType(resultSet.getString("type"));
			identifier.setDatabase(resultSet.getString("db_name"));

			String typeClass = resultSet.getString("type_class");
			identifier.setDatabaseCategory(DB_TYPE_NP1_NAMES.containsKey(typeClass) ? DB_TYPE_NP1_NAMES.get(typeClass) : typeClass);

			return identifier;
		}
	}
}
