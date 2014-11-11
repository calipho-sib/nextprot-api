package org.nextprot.api.commons.utils;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

public class KeyValuesJdbcBatchUpdaterTest {

    @Test
    public void test() throws Exception {

        JdbcTemplate template = Mockito.mock(JdbcTemplate.class);

        KeyValuesJdbcBatchUpdater updater = new KeyValuesJdbcBatchUpdater(template, 1) {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            }
        };

        List<String> list = new ArrayList<String>();

        for (int i=0 ; i<101 ; i++) {

            list.add(String.valueOf(i));
        }

        updater.batchUpdate("insert ...", list);

        Mockito.verify(template, times(1)).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }

    @Test
    public void testWithBatchSize() throws Exception {

        JdbcTemplate template = Mockito.mock(JdbcTemplate.class);

        KeyValuesJdbcBatchUpdater updater = new KeyValuesJdbcBatchUpdater(template, 1, 10) {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            }
        };

        List<String> list = new ArrayList<String>();

        for (int i=0 ; i<101 ; i++) {

            list.add(String.valueOf(i));
        }

        updater.batchUpdate("insert ...", list);

        Mockito.verify(template, times(11)).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }
}