package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class GeneIdentifierDaoImpl implements GeneIdentifierDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public Set<String> findGeneNames() {
		return new HashSet<>(new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("all-gene-names"), String.class));
	}
}
