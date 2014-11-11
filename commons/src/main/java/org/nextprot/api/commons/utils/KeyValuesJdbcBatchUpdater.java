package org.nextprot.api.commons.utils;

import org.nextprot.api.commons.exception.NPreconditions;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * This object inserts multiple key/values in batch mode
 *
 * @author fnikitin
 */
public abstract class KeyValuesJdbcBatchUpdater implements BatchPreparedStatementSetter {

    /** the maximum number of statements in the batch to update with JdbcTemplate */
    private static final int JDBC_TEMPLATE_BATCH_SIZE = 200;

    private final JdbcTemplate jdbcTemplate;
    private final long key;
    private final int maxBatchSize;

    private List<String> currentBatch;

    protected KeyValuesJdbcBatchUpdater(JdbcTemplate jdbcTemplate, long key) {

        this(jdbcTemplate, key, JDBC_TEMPLATE_BATCH_SIZE);
    }

    protected KeyValuesJdbcBatchUpdater(JdbcTemplate jdbcTemplate, long key, int maxBatchSize) {

        NPreconditions.checkNotNull(jdbcTemplate, "jdbcTemplate is undefined");
        NPreconditions.checkTrue(maxBatchSize>0, "maxBatchSize is less equals than 0");

        this.key = key;
        this.maxBatchSize = maxBatchSize;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchUpdate(String sql, List<String> values) {

        NPreconditions.checkNotNull(sql, "sql is undefined");
        NPreconditions.checkTrue(sql.length()>0, "sql is empty");
        NPreconditions.checkNotNull(values, "values are undefined");

        for (int j = 0; j < values.size(); j += maxBatchSize) {

            currentBatch = values.subList(j, j + maxBatchSize > values.size() ? values.size() : j + maxBatchSize);

            jdbcTemplate.batchUpdate(sql, this);
        }
    }

    public long getKey() {

        return key;
    }

    public String getValue(int i) {

        return currentBatch.get(i);
    }

    @Override
    public int getBatchSize() {

        return currentBatch.size();
    }
}