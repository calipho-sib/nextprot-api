package org.nextprot.api.etl.app;

import org.nextprot.commons.statements.NXFlatTableStatementField;
import org.nextprot.commons.statements.constants.StatementTableNames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GenerateNXFlatStatementDDLApp {

	public static String generateDDL() {
		StringBuffer sb = new StringBuffer();
		sb.append(generateOneTable(StatementTableNames.ENTRY_TABLE));
		sb.append(generateOneTable(StatementTableNames.RAW_TABLE));
		return sb.toString();
	}

	private static String generateOneTable(String tableName) {

		StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE IF EXISTS nxflat." + tableName + ";\n");
		sb.append("CREATE TABLE nxflat." + tableName + " (\n");
		for (int i = 0; i < NXFlatTableStatementField.values().length; i++) {
			sb.append("\t" + NXFlatTableStatementField.values()[i].name() + " VARCHAR(10000)");
			if (i + 1 < NXFlatTableStatementField.values().length) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append(");\n");

		sb.append("CREATE INDEX " + tableName.substring(0, 10) + "_ENTRY_AC_IDX ON nxflat." + tableName + " ( " + NXFlatTableStatementField.ENTRY_ACCESSION.name() + " );\n");
		sb.append("CREATE INDEX " + tableName.substring(0, 10) + "_ANNOT_ID_IDX ON nxflat." + tableName + " ( " + NXFlatTableStatementField.ANNOTATION_ID.name() + " );\n");
		sb.append("\n\n");

		return sb.toString();

	}

	
	public static void main(String[] args) throws Exception {
		String ddl = generateDDL();

		File file = new File("../commons/src/main/resources/nxflat-statements-schema.ddl");
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
