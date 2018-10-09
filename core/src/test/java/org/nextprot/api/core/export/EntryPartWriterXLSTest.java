package org.nextprot.api.core.export;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.util.Iterator;

@ActiveProfiles({ "dev","cache" })
public class EntryPartWriterXLSTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void getExpressionProfileOutputString() throws Exception {

        File tempFile = testFolder.newFile("out.xls");

        OutputStream fos = new FileOutputStream(tempFile);

        String subpart = "expression-profile";
        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").with(subpart));

        EntryPartWriterXLS writer = new EntryPartWriterXLS(EntryPartExporterImpl.fromSubPart(subpart), fos);
        writer.write(entry);

        fos.close();

        assertCountsGreaterThan(500, 13, tempFile);
    }

    private static void assertCountsGreaterThan(int expectedRowCount, int expectedColumnCount, File file) throws IOException {

        FileInputStream excelFile = new FileInputStream(file);
        Workbook workbook = new HSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();

        int rowCount = 0;

        while (iterator.hasNext()) {

            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();

            int columnCount = 0;
            while (cellIterator.hasNext()) {
                cellIterator.next();
                columnCount++;
            }

            Assert.assertEquals(expectedColumnCount, columnCount);
            rowCount++;
        }
        Assert.assertTrue(rowCount >= expectedRowCount);
    }
}