package org.nextprot.api.commons.dbunit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Ignore
@ActiveProfiles({"unit", "unit-schema-nextprot"})
public class GenerateDTD extends CommonsUnitBaseTest {

	private static final String dtdFile = "nextprot.dtd";

	@Autowired DataSourceServiceLocator dsLocator;

	@Test
	public void generateDTD() throws Exception {

		IDatabaseConnection connection = new DatabaseConnection(dsLocator.getDataSource().getConnection());
		// write DTD file 
		IDataSet dataSet = connection.createDataSet();
		Writer out = new OutputStreamWriter(new FileOutputStream(dtdFile));
        FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
        datasetWriter.setContentModel(FlatDtdWriter.CHOICE);
        // You could also use the sequence model which is the default
        // datasetWriter.setContentModel(FlatDtdWriter.SEQUENCE);
        datasetWriter.write(dataSet);
        //delete file after the test
        new File(dtdFile).delete();
	}
	
	
	@Test
	public void generateUserDTD() throws Exception {

		IDatabaseConnection connection = new DatabaseConnection(dsLocator.getUserDataSource().getConnection());
		// write DTD file 
		IDataSet dataSet = connection.createDataSet();
		Writer out = new OutputStreamWriter(new FileOutputStream("user.dtd"));
        FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
        datasetWriter.setContentModel(FlatDtdWriter.CHOICE);
        // You could also use the sequence model which is the default
        // datasetWriter.setContentModel(FlatDtdWriter.SEQUENCE);
        datasetWriter.write(dataSet);
        //delete file after the test
        new File(dtdFile).delete();
	}
}
