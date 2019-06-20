package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GeneIdentifierDaoImpl implements GeneIdentifierDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public Set<String> findGeneNames() {
		return new TreeSet<>(new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("all-gene-names"), String.class));
	}

	@Override
	public Map<String, List<String>> findEntryGeneNames() {

		Map<String, List<String>> map = new TreeMap<>();

		new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("all-accessions-with-gene-names"))
				.forEach(row -> {
					String entryName = (String) row.get("unique_name");

					if (!map.containsKey(entryName)) {
						map.put(entryName, new ArrayList<>());
					}
					map.get(entryName).add((String) row.get("gene_name"));
				});

		map.values().forEach(geneNames -> geneNames.sort(String::compareTo));

		return map;
	}

	@Override
	public String findGeneNameByChromosomalLocation(ChromosomalLocation chromosomalLocation) {
		// Finds the gene name for the given chromosomal location
		String chromosome = chromosomalLocation.getChromosome();
		int firstPosition = chromosomalLocation.getFirstPosition();
		int lastPosition = chromosomalLocation.getLastPosition();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("chromosome", chromosome);
		parameters.put("first_position", firstPosition);
		parameters.put("last_position", lastPosition);
		SqlParameterSource parameterSource = new MapSqlParameterSource(parameters);
		String geneName = new NamedParameterJdbcTemplate(dsLocator.getDataSource())
				.queryForObject(sqlDictionary.getSQLQuery("gene-entry-by-chromosomal-location"), parameterSource, String.class);
		return geneName;
	}
}
