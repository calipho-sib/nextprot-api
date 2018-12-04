package org.nextprot.api.commons.utils;

import org.nextprot.api.commons.exception.NPreconditions;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utilitary methods for jdbcTemplate
 *
 * @author fnikitin
 */
public class JdbcUtils {

    /**
     * Insert a row and returns a key number generated for the given column name
     *
     * @param sql the SQL statement
     * @param keyColumnName name of the column that will have a generated key
     * @param namedParameters a container of arguments and SQL types to bind to the query
     * @param jdbcOperations JDBC operations allowing the use of named parameters
     * @return a numeric generated key
     */
    public static Number insertAndGetKey(String sql, String keyColumnName, SqlParameterSource namedParameters, NamedParameterJdbcOperations jdbcOperations) {

        return insertAndGetKey(sql, keyColumnName, new GeneratedKeyHolder(), namedParameters, jdbcOperations);
    }

    static Number insertAndGetKey(String sql, String columnName, KeyHolder keyHolder, SqlParameterSource namedParameters, NamedParameterJdbcOperations jdbcOperations) {

        NPreconditions.checkNotNull(sql, "undefined SQL query");
        NPreconditions.checkTrue(sql.length()>0, "empty SQL query");
        NPreconditions.checkNotNull(columnName, "undefined column name");
        NPreconditions.checkTrue(columnName.length()>0, "empty column name");
        NPreconditions.checkNotNull(keyHolder, "undefined key holder");
        NPreconditions.checkNotNull(namedParameters, "undefined SQL parameters");
        NPreconditions.checkNotNull(jdbcOperations, "undefined JDBC operations");

        int affectedRow = jdbcOperations.update(sql, namedParameters, keyHolder, new String[] { columnName });

        NPreconditions.checkTrue(affectedRow == 1, affectedRow+": expected 1 affected row");
        NPreconditions.checkNotNull(keyHolder.getKey(), "no key was generated for column '" + columnName + "'");

        return keyHolder.getKey();
    }

    public static class LongRowMapper extends SingleColumnRowMapper<Long> {

        private final String columnName;

        public LongRowMapper(String columnName) {
            this.columnName = columnName;
        }

        @Override
        public Long mapRow(ResultSet resultSet, int row) throws SQLException {
            return resultSet.getLong(columnName);
        }
    }
}
