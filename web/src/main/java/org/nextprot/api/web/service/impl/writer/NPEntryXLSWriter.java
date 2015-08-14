package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Export nextprot entries in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public abstract class NPEntryXLSWriter extends NPEntryOutputStreamWriter {

    private final HSSFWorkbook workbook;
    private final HSSFSheet worksheet;
    private final HSSFCellStyle hlinkStyle;

    private int rowIndex;

    public static class Record {

        private Object[] values;
        private int[] stringValueIndices = new int[0];
        private int[] intValueIndices = new int[0];
        private int[] doubleValueIndices = new int[0];
        private int[] booleanValueIndices = new int[0];
        private Map<Integer, String> hyperLinks = new HashMap<>();

        public Object[] getValues() {
            return values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }

        // @return indices which values are of type String
        public int[] getStringValueIndices() {
            return stringValueIndices;
        }

        public void setStringValueIndices(int[] stringValueIndices) {
            this.stringValueIndices = stringValueIndices;
        }

        // @return indices which values are of type int
        public int[] getIntValueIndices() {
            return intValueIndices;
        }

        public void setIntValueIndices(int[] intValueIndices) {
            this.intValueIndices = intValueIndices;
        }

        // @return indices which values are of type float or double
        public int[] getDoubleValueIndices() {
            return doubleValueIndices;
        }

        public void setDoubleValueIndices(int[] doubleValueIndices) {
            this.doubleValueIndices = doubleValueIndices;
        }

        // @return indices which values are boolean
        public int[] getBooleanValueIndices() {
            return booleanValueIndices;
        }

        public void setBooleanValueIndices(int[] booleanValueIndices) {
            this.booleanValueIndices = booleanValueIndices;
        }

        // Define hyperlinks for specific cells
        public Map<Integer, String> getHyperLinks() {
            return hyperLinks;
        }

        public void addHyperLinks(int index, String address) {

            hyperLinks.put(index, address);
        }
    }

    public interface WritingRowStrategy {

        // Get the EntryBlocks needed to get values and build this row
        List<EntryBlock> getSourceEntryBlocks();

        // Get the names of column values
        String[] getHeaders();

        // @return a list of records (a record maps a row in excel)
        List<Record> getRecords(Entry entry);
    }

    private WritingRowStrategy strategy;

    protected NPEntryXLSWriter(OutputStream stream, String sheetName, WritingRowStrategy strategy) {

        super(stream);

        Preconditions.checkNotNull(strategy);

        workbook = new HSSFWorkbook();
        hlinkStyle = createHLinkStyle(workbook);
        worksheet = workbook.createSheet(sheetName);
        rowIndex = 0;

        this.strategy = strategy;
    }

    private static HSSFCellStyle createHLinkStyle(HSSFWorkbook workbook) {

        HSSFCellStyle ls = workbook.createCellStyle();

        HSSFFont hlinkFont = workbook.createFont();
        hlinkFont.setUnderline(HSSFFont.U_SINGLE);
        hlinkFont.setColor(HSSFColor.BLUE.index);
        ls.setFont(hlinkFont);

        return ls;
    }

    @Override
    protected void writeHeader(Map<String, Object> headerParams) throws IOException {

        String[] headers = strategy.getHeaders();

        HSSFRow row = worksheet.createRow(rowIndex);

        for (int i=0 ; i<headers.length ; i++) {

            HSSFCell accCodeCell = row.createCell(i);
            accCodeCell.setCellValue(headers[i]);
        }

        rowIndex++;
    }

    private void setHyperLink(HSSFCell cell, String address) {

        HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link.setAddress(address);
        cell.setHyperlink(link);
        cell.setCellStyle(hlinkStyle);
    }

    // http://dev-api.nextprot.org/export/entries/accession.xls?query=kimura-matsumoto
    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        EntryConfig config = EntryConfig.newConfig(entryName);

        for (EntryBlock block : strategy.getSourceEntryBlocks()) {

            config.withBlock(block);
        }

        Entry entry = entryBuilderService.build(config);

        for (Record record : strategy.getRecords(entry)) {

            HSSFRow row = worksheet.createRow(rowIndex);
            writeRecord(row, record);

            rowIndex++;
        }
    }

    private void writeRecord(HSSFRow row, Record record) {

        Object[] values = record.getValues();

        HSSFCell[] cells = new HSSFCell[values.length];

        for (int index : record.getStringValueIndices()) {

            cells[index] = row.createCell(index);
            cells[index].setCellValue((String)values[index]);
        }

        for (int index : record.getBooleanValueIndices()) {

            cells[index] = row.createCell(index);
            cells[index].setCellValue((boolean)values[index]);
        }

        for (int index : record.getIntValueIndices()) {

            cells[index] = row.createCell(index);
            cells[index].setCellValue((int)values[index]);
        }

        for (int index : record.getDoubleValueIndices()) {

            cells[index] = row.createCell(index);
            cells[index].setCellValue((double)values[index]);
        }

        Map<Integer, String> links = record.getHyperLinks();

        for (int index : links.keySet()) {

            setHyperLink(cells[index], links.get(index));
        }
    }

    @Override
    public void close() throws IOException {

        workbook.write(stream);
        workbook.close();
    }
}
