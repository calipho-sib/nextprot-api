package org.nextprot.api.etl.app;

import org.nextprot.commons.statements.constants.StatementTableNames;
import org.nextprot.commons.statements.specs.NXFlatTableSchema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GenerateNXFlatStatementDDLApp {

	public static String generateDDL() {

		NXFlatTableSchema schema = new NXFlatTableSchema();

		StringBuffer sb = new StringBuffer();
		sb.append(schema.generateCreateTableInSQL(StatementTableNames.ENTRY_TABLE));
		sb.append(schema.generateCreateTableInSQL(StatementTableNames.RAW_TABLE));
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		String ddl = generateDDL();
		File file = new File("etl-statement/src/main/resources/nxflat-statements-schema.ddlsss");
	    if (!file.exists()) {
            file.createNewFile();
        }
        
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(ddl);
		bw.close();
		
		System.out.println("nxflat ddl written to " + file.getAbsolutePath());
	}
}
