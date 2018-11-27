package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class to export nextprot entries in XLS format.
 *
 * Need an implementation of <code>EntryDataProvider</code> to handle extraction of data in Records
 * for XLS sheet rows and cells to be properly created.
 *
 * Created by fnikitin on 11/08/15.
 */
public abstract class EntryXLSWriter extends EntryOutputStreamWriter {

    private final HSSFWorkbook workbook;
    private final HSSFSheet worksheet;
    private final HSSFCellStyle hlinkStyle;

    private int rowIndex;

    /** A record represents field values to be written into an XLS row */
    public static class Record {

        private final Object[] values;
        private int[] stringValueIndices = new int[0];
        private int[] intValueIndices = new int[0];
        private int[] doubleValueIndices = new int[0];
        private int[] booleanValueIndices = new int[0];
        private Map<Integer, String> hyperLinks = new HashMap<>();

        public Record(Object[] values) {

            Preconditions.checkNotNull(values);

            this.values = values;
        }

        public Object[] getValues() {
            return values;
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

    /** Provides nextprot entry data to this XLS writer */
    public interface EntryDataProvider {

        // @return the EntryBlocks needed to get data (equivalent to view names)
        List<EntryBlock> getSourceEntryBlocks();

        // @return the field names
        String[] getFieldNames();

        // @return a list of data records (a record contain the field values) of the given entry
        List<Record> getRecords(Entry entry);
    }

    private final EntryDataProvider entryDataProvider;

    protected EntryXLSWriter(OutputStream stream, String sheetName, EntryDataProvider entryDataProvider, ApplicationContext applicationContext) {

        super(stream, applicationContext);

        Preconditions.checkNotNull(entryDataProvider);

        workbook = new HSSFWorkbook();
        hlinkStyle = createHLinkStyle(workbook);
        worksheet = workbook.createSheet(sheetName);
        rowIndex = 0;

        this.entryDataProvider = entryDataProvider;
    }

    public static EntryXLSWriter newNPEntryXLSWriter(OutputStream os, String viewName, ApplicationContext applicationContext) {

        if (viewName.equals("isoforms"))
            return new EntryIsoformXLSWriter(os, applicationContext);
        else
            return new EntryOverviewXLSWriter(os, applicationContext);
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
    public void write(Collection<String> entries, Map<String, Object> infos) throws IOException {

        super.write(entries, infos);

        workbook.write(getStream());
    }

    @Override
    protected void writeHeader(Map<String, Object> infos) throws IOException {

        String[] headers = entryDataProvider.getFieldNames();

        HSSFRow row = worksheet.createRow(rowIndex);

        for (int i=0 ; i<headers.length ; i++) {

            HSSFCell accCodeCell = row.createCell(i);
            accCodeCell.setCellValue(headers[i]);
        }

        rowIndex++;
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        EntryConfig config = EntryConfig.newConfig(entryName);

        for (EntryBlock block : entryDataProvider.getSourceEntryBlocks()) {

            config.withBlock(block);
        }

        Entry entry = entryBuilderService.build(config);

        for (Record record : entryDataProvider.getRecords(entry)) {

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
            cells[index].setCellValue((double) values[index]);
        }

        Map<Integer, String> links = record.getHyperLinks();

        for (int index : links.keySet()) {

            setHyperLink(cells[index], links.get(index));
        }
    }

    // No need to flush after each entry has been written
    protected void flush() throws IOException { }

    private void setHyperLink(HSSFCell cell, String address) {

        HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link.setAddress(address);
        cell.setHyperlink(link);
        cell.setCellStyle(hlinkStyle);
    }

    @Override
    public void close() throws IOException {

        workbook.close();
    }
}
