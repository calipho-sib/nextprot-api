package org.nextprot.api.jdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Replaces NamedParameterJdbcTemplate when the list of ids it too big
 * JDBC prepared statement has a limit of 32767, therefore if the number of ids is bigger a not so obvious exception is thrown: the PostgreSQL client/backend protocol dictates that the number of parameters be send from the client to the Postgres backend as a 2 byte integer (aaah, now the above message actually makes sense). You’ll find details of the protocol 
 * This class allows to send the request in batch of 32767 if the list of ids is bigger
 * Don't use this class if your list of ids is not that big 
 * 
 * @author dteixeira
 *
 */
public class BatchNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

	private static final int JDBC_PARAM_LIMIT = 32767;
	private static final Log LOGGER = LogFactory.getLog(BatchNamedParameterJdbcTemplate.class);

	public BatchNamedParameterJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Used to query in batch
	 * @param sql The sql
	 * @param paramIdsName the name of the bind variable
	 * @param paramIds the list of ids (bigger than 32767)
	 * @param rowMapper the row mapper
	 * @return
	 */
	public <T> List<T> query(String sql, String paramIdsName, List<Long> paramIds, RowMapper<T> rowMapper) {

		Set<T> result = new HashSet<T>();

		Queue<Long> keys = new LinkedList<Long>(paramIds);

		int bcount = 0;
		while (!keys.isEmpty()) {

			List<Long> subIds = new ArrayList<Long>();
			for (int i = 0; i <  JDBC_PARAM_LIMIT && !keys.isEmpty(); i++) {
				subIds.add(keys.remove());
			}

			MapSqlParameterSource subParams = new MapSqlParameterSource("ids", subIds);
			
			LOGGER.debug("Sending batch" + bcount + " of " + subIds.size() + " size");
			result.addAll(super.query(sql, subParams, rowMapper));
		}

		return new ArrayList<T>(result);
	}
}
