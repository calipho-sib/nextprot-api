package org.nextprot.api.user.dao.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

/**
 * An abstract callback class used by the {@link org.springframework.jdbc.core.JdbcTemplate} class
 * to batch multiple update statements on a single PreparedStatement.
 *
 * <p>
 * This class is to be called as a single reusable instance by int[] batchUpdate(String sql, BatchPreparedStatementSetter pss)
 * </p>
 *
 * <H2>Example</H2>
 *
 *<pre>
 *UserProteinBatchSetter userProteinBatchSetter = new UserProteinBatchSetter(listId, accessionNumbers.size());
 *
 *for (final String accessionNumber : accessionNumbers) {
 *
 *  // set
 *  userProteinBatchSetter.setAccessionNumber(accessionNumber);
 *
 *  jdbcTemplate.batchUpdate(sql, userProteinBatchSetter);
 *}
 *</pre>
 *
 * @author fnikitin
 */
abstract class ReusableBatchSetter implements BatchPreparedStatementSetter {

    private final long id;
    private final int size;

    protected ReusableBatchSetter(long id, int size) {

        NPreconditions.checkTrue(size > 0, "batch size is empty");

        this.size = size;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    @Override
    public int getBatchSize() {
        return size;
    }
}