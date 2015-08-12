package org.nextprot.api.web.service.impl.writer;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Export entries in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public class NPEntryXLSWriter extends NPEntryOutputStreamWriter {

    private final HSSFWorkbook workbook;
    private final HSSFSheet worksheet;
    private final HSSFCellStyle hlinkStyle;

    private int rowIndex;

    public NPEntryXLSWriter(OutputStream stream) {

        super(stream);

        workbook = new HSSFWorkbook();
        hlinkStyle = createHLinkStyle(workbook);
        worksheet = workbook.createSheet("Proteins");
        rowIndex = 0;
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

        String[] headers = new String[] { "acc. code", "name", "gene name(s)", "chromosome", "proteomics", "disease",	"structure", "#isof.", "#variants", "#PTMS", "mutagenesis", "tissue expr.", "PE" };

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

    private static String booleanToYesNoString(boolean bool) {

        return (bool) ? "yes" : "no";
    }

    // http://dev-api.nextprot.org/export/entries/accession.xls?query=kimura-matsumoto
    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName)
                        .withBlock(EntryBlock.OVERVIEW)
                        .withBlock(EntryBlock.CHROMOSOMAL_LOCATION));

        /**
         * acc. code / name             / gene name(s) / chromosome / proteomics / disease	/ structure	/ #isof. / #variants / #PTMS / mutagenesis / tissue expr. /	PE
         * -----------------------------------------------------------------------------------------------------------------------------------------------------------
         * NX_P48730 / Casein kinase .. / CSNK1        / 17q25.3    / yes	     / yes	    / yes	    / 2	     / 41	     / 8	 / yes         / yes          / Evidence at protein level
         */
        HSSFRow row = worksheet.createRow(rowIndex);

        HSSFCell accCodeCell = row.createCell(0);
        accCodeCell.setCellValue(entryName);
        setHyperLink(accCodeCell, "http://www.nextprot.org/db/entry/" + entryName);

        HSSFCell nameCell = row.createCell(1);
        nameCell.setCellValue(entry.getOverview().getMainProteinName());

        HSSFCell geneNamesCell = row.createCell(2);
        geneNamesCell.setCellValue(entry.getOverview().getMainGeneName());

        HSSFCell chromosomeCell = row.createCell(3);
        ChromosomalLocation location = entry.getChromosomalLocations().get(0);
        chromosomeCell.setCellValue(location.getChromosome() + location.getBand());

        HSSFCell proteomicsCell = row.createCell(4);
        proteomicsCell.setCellValue(booleanToYesNoString(entry.getProperties().getFilterproteomics()));

        HSSFCell diseaseCell = row.createCell(5);
        diseaseCell.setCellValue(booleanToYesNoString(entry.getProperties().getFilterdisease()));

        HSSFCell structureCell = row.createCell(6);
        structureCell.setCellValue(booleanToYesNoString(entry.getProperties().getFilterstructure()));

        HSSFCell isoformCountCell = row.createCell(7);
        isoformCountCell.setCellValue(entry.getProperties().getIsoformCount());

        HSSFCell variantCountCell = row.createCell(8);
        variantCountCell.setCellValue(entry.getProperties().getVarCount());

        HSSFCell ptmCountCell = row.createCell(9);
        ptmCountCell.setCellValue(entry.getProperties().getPtmCount());

        HSSFCell mutagenesisCell = row.createCell(10);
        mutagenesisCell.setCellValue(booleanToYesNoString(entry.getProperties().getFiltermutagenesis()));

        HSSFCell tissueExpressionCell = row.createCell(11);
        tissueExpressionCell.setCellValue(booleanToYesNoString(entry.getProperties().getFilterexpressionprofile()));

        HSSFCell proteinEvidenceCell = row.createCell(12);
        proteinEvidenceCell.setCellValue(entry.getProperties().getProteinExistence());

        rowIndex++;
    }

    @Override
    public void lastFlush() throws IOException {

        workbook.write(stream);
    }
}
