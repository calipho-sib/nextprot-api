package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReleaseInfoDaoImpl implements ReleaseInfoDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;
	@Autowired
	private SQLDictionary sqlDictionary;

	@Override
	public String findDatabaseRelease() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("nextprot-release"), String.class);
	}
}
