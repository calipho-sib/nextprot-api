package org.nextprot.api.core.service;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility object that extract data lsit from dbunit xml file
 *
 * Created by fnikitin on 21/05/15.
 */
public class FlatXmlDataSetExtractor {

    private final FlatXmlDataSet ds;

    public FlatXmlDataSetExtractor(String xmlFilename) throws FileNotFoundException, DataSetException {

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        ds = builder.build(new FileInputStream(xmlFilename));
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        try {
            for (String tableName : ds.getTableNames()) {

                sb.append("Table:").append(tableName).append("\n");

                ITable table = ds.getTable(tableName);

                for (int i=0 ; i<table.getRowCount() ; i++) {

                    ITableMetaData md = table.getTableMetaData();

                    sb.append("    ");
                    for (Column col : md.getColumns()) {

                        sb.append(col.getColumnName()).append(": ").append(table.getValue(i, col.getColumnName()));
                        sb.append(", ");
                    }
                    sb.delete(sb.length()-2, sb.length());
                    sb.append("\n");
                }
            }
        } catch (DataSetException e) {

            sb.append("Error: ").append(e.getMessage());
        }

        return sb.toString();
    }

    public interface Factory<A> {

        A create();
        void setField(A instance, String key, String value);
    }

    public <A> List<A> extractDataList(String tableName, Factory<A> factory, String... fields) throws DataSetException {

        ITable table = ds.getTable(tableName);

        List<A> dataList = new ArrayList<>();

        for (int i=0 ; i<table.getRowCount() ; i++) {

            A element = factory.create();

            for (int j=0 ; j<fields.length ; j++) {
                if (table.getValue(i, fields[j]) != null) {
                    factory.setField(element, fields[j], table.getValue(i, fields[j]).toString());
                }
            }

            dataList.add(element);
        }

        return dataList;
    }
}
