package org.nextprot.api.web.service.impl.writer;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;

import java.io.*;
import java.util.Arrays;

@Ignore
public class EntryOverviewXLSWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXLSOverviewWriterStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EntryXLSWriter writer = new EntryOverviewXLSWriter(out, wac);

        writer.write(Arrays.asList("NX_P48730"));
        writer.close();

        assertXLSEquals(out, new String[] { "acc. code", "protein name", "gene name(s)", "chromosome", "proteomics", "disease",	"structure", "#isof.", "#variants", "#PTMS", "mutagenesis", "tissue expr.", "PE" },
                new Object[] { "NX_P48730","Casein kinase I isoform delta","CSNK1D","17q25.3","yes","yes","yes",2.0,54.0,8.0,"yes","yes","Evidence at protein level"});
    }

    //@Test
    public void exportXLSFile() throws Exception {

        FileOutputStream out = new FileOutputStream("/Users/fnikitin/Downloads/proteins.xls");

        EntryXLSWriter writer = new EntryOverviewXLSWriter(out, wac);

        writer.write(Arrays.asList("NX_P48730"));
    }

    private static void assertXLSEquals(ByteArrayOutputStream baos, String[] headers, Object[] values) throws IOException {

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet worksheet = workbook.getSheet("Proteins");

        HSSFRow headerRow = worksheet.getRow(0);
        HSSFRow valuesRow = worksheet.getRow(1);

        for (int i=0 ; i<headers.length ; i++) {
            Assert.assertEquals(headers[i], headerRow.getCell(i).getStringCellValue());
            if (i>=7 && i<=9)
                Assert.assertEquals(values[i], valuesRow.getCell(i).getNumericCellValue());
            else
                Assert.assertEquals(values[i], valuesRow.getCell(i).getStringCellValue());
        }
        
        workbook.close();

    }
}