package org.nextprot.api.user;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

//@ActiveProfiles({"unit", "unit-schema-nextprot"})
public class GenerateUserDTD extends UserApplicationBaseTest {

	private static final String dtdFile = "user.dtd";

	@Autowired DataSourceServiceLocator dsLocator;

    /**
     * This method should be executed every times the db schema change.
     * The schema is defined in main/resources/db.migration/*.sql.
     * The generated file user.dtd should go
     * @throws Exception
     */
	@Test
	public void generateUserDTD() throws Exception {

		IDatabaseConnection connection = new DatabaseConnection(dsLocator.getUserDataSource().getConnection());
		// write DTD file 
		IDataSet dataSet = connection.createDataSet();
		Writer out = new OutputStreamWriter(new FileOutputStream(dtdFile));
        FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
        datasetWriter.setContentModel(FlatDtdWriter.CHOICE);
        datasetWriter.write(dataSet);
	}
}
