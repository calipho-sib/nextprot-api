package org.nextprot.api.web.service.impl.writer;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;

import java.io.*;
import java.util.Arrays;

public class EntryIsoformXLSWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXLSIsoformWriterStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EntryXLSWriter writer = new EntryIsoformXLSWriter(out, wac);

        writer.write(Arrays.asList("NX_P48730"));
        writer.close();

        assertXLSEquals(out, new String[]{"acc. code", "protein name", "isoform", "seq. len", "mass", "PI"},
                new Object[]{"NX_P48730-1", "Casein kinase I isoform delta", "Iso 1", 415.0, 47330.0, 9.77},
                new Object[]{"NX_P48730-2", "Casein kinase I isoform delta", "Iso 2", 409.0, 46832.0, 9.67}
        );
    }

    //@Test
    public void exportXLSIsoformsFile() throws Exception {

        FileOutputStream out = new FileOutputStream("/Users/fnikitin/Downloads/isoforms.xls");

        EntryXLSWriter writer = new EntryIsoformXLSWriter(out, wac);

        writer.write(Arrays.asList("NX_P48730"));
    }

    private static void assertXLSEquals(ByteArrayOutputStream baos, String[] headers, Object[]... expectedRows) throws IOException {

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet worksheet = workbook.getSheet("Isoforms");

        HSSFRow headerRow = worksheet.getRow(0);

        for (int rowIndex=0 ; rowIndex<expectedRows.length ; rowIndex++) {

            Object[] expectedRow = expectedRows[rowIndex];

            HSSFRow valuesRow = worksheet.getRow(rowIndex+1);

            for (int i = 0; i < headers.length; i++) {
                Assert.assertEquals(headers[i], headerRow.getCell(i).getStringCellValue());
                if (i > 2)
                    Assert.assertEquals(expectedRow[i], valuesRow.getCell(i).getNumericCellValue());
                else
                    Assert.assertEquals(expectedRow[i], valuesRow.getCell(i).getStringCellValue());
            }
        }
        
        workbook.close();
    }
}