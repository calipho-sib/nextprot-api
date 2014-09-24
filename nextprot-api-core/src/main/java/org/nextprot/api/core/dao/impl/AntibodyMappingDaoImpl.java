package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class AntibodyMappingDaoImpl implements AntibodyMappingDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<AntibodyMapping> findAntibodiesById(Long id) {
		
		SqlParameterSource namedParams = new MapSqlParameterSource("id", id);

		// step 1 - one object per each antibody - isoform - position
		List<AntibodyMapping> flatmaps =  new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(SQLDictionary.getSQLQuery("antibodies-by-id"), namedParams, new RowMapper<AntibodyMapping>() {
			@Override
			public AntibodyMapping mapRow(ResultSet resultSet, int row) throws SQLException {
				AntibodyMapping antibodyMapping = new AntibodyMapping();
				antibodyMapping.setXrefId(resultSet.getLong("db_xref_id"));
				antibodyMapping.setAntibodyUniqueName(resultSet.getString("antibody_unique_name"));
				IsoformSpecificity isoformSpecificity = new IsoformSpecificity(resultSet.getString("iso_unique_name"));
				isoformSpecificity.addPosition(resultSet.getInt("first_pos"), resultSet.getInt("last_pos"));
				antibodyMapping.addIsoformSpecificity(isoformSpecificity);
				return antibodyMapping;
			}
		});
		// step 2 - one object per antibody with nested isoform specs, spec with nested map positions
		Map<String,AntibodyMapping> mergedmap = new HashMap<String,AntibodyMapping>();
		for (AntibodyMapping map : flatmaps) {
			String ab = map.getAntibodyUniqueName();
			if (!mergedmap.containsKey(ab)) {
				mergedmap.put(ab, map);
			} else {
				AntibodyMapping mapIn = mergedmap.get(ab);
				mapIn.addIsoformSpecificity(map.getFirstIsoformSpecificity());
			}
		}
		return new ArrayList<AntibodyMapping>(mergedmap.values());
	}
}
