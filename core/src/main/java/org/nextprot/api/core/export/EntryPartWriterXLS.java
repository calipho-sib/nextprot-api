package org.nextprot.api.core.export;

import org.apache.poi.hssf.usermodel.*;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.export.EntryPartExporter;
import org.nextprot.api.core.export.EntryPartWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Writes entry parts in excel format
 * Delegate the type of infos to export to {@code EntryPartExporter}
 */
public class EntryPartWriterXLS extends EntryPartWriter {

    private final EntryPartExporter exporter;

    private final HSSFWorkbook workbook;
    private final HSSFSheet worksheet;

    private int rowIndex;

    public EntryPartWriterXLS(EntryPartExporter exporter, OutputStream os) {

        super(os);
        this.exporter = exporter;

        workbook = new HSSFWorkbook();
        worksheet = workbook.createSheet("export");
        rowIndex = 0;
    }

    @Override
    public void writeHeader() throws IOException {

        HSSFRow row = worksheet.createRow(rowIndex);

        List<EntryPartExporter.Header> headers = exporter.exportHeaders();

        for (int i=0 ; i<headers.size() ; i++) {

            HSSFCell accCodeCell = row.createCell(i);
            accCodeCell.setCellValue(headers.get(i).name());
        }

        rowIndex++;
    }

    @Override
    public void writeRows(Entry entry) throws IOException {

        List<EntryPartExporter.Row> rows = exporter.exportRows(entry);

        for (int i=0 ; i<rows.size() ; i++) {

            HSSFRow row = worksheet.createRow(rowIndex);
            writeRow(row, rows.get(i));

            rowIndex++;
        }
    }

    @Override
    protected void flush() throws IOException {

        workbook.write(getOutputStream());
        super.flush();
    }

    private void writeRow(HSSFRow row, EntryPartExporter.Row record) {

        HSSFCell[] cells = new HSSFCell[record.size()];

        for (int i=0 ; i<record.size() ; i++) {

            cells[i] = row.createCell(i);
            cells[i].setCellValue(record.getValue(i));
        }
    }
}
