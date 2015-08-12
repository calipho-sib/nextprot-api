package org.nextprot.api.web.service.impl.writer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Export entries in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public class NPEntryXLSWriter extends NPEntryOutputStreamWriter {

    private final HSSFWorkbook workbook;
    private final HSSFSheet worksheet;

    private int rowIndex;

    private final OutputStream os;

    public NPEntryXLSWriter(OutputStream os) {

        super(os);

        workbook = new HSSFWorkbook();
        worksheet = workbook.createSheet("Proteins");

        rowIndex = 0;

        this.os = os;
    }

    // http://dev-api.nextprot.org/export/entries/accession.xml?query=kimuramatsumoto
    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).with(viewName));

        /**
         * acc. code / name             / gene name(s) / chromosome / proteomics / disease	/ structure	/ #isof. / #variants / #PTMS / mutagenesis / tissue expr. /	PE
         * -----------------------------------------------------------------------------------------------------------------------------------------------------------
         * NX_P48730 / Casein kinase .. / CSNK1        / 17q25.3    / yes	     / yes	    / yes	    / 2	     / 41	     / 8	 / yes         / yes          / Evidence at protein level
         */
        // index from 0,0... cell A1 is cell(0,0)
        HSSFRow row = worksheet.createRow(rowIndex);

        HSSFCell accCodeCell = row.createCell(0);
        accCodeCell.setCellValue(entryName);

        HSSFCell nameCell = row.createCell(1);
        nameCell.setCellValue(entry.getOverview().getMainProteinName());

        HSSFCell geneNamesCell = row.createCell(2);
        geneNamesCell.setCellValue(entry.getOverview().getMainGeneName());

        HSSFCell chromosomeCell = row.createCell(3);
        chromosomeCell.setCellValue(entry.getChromosomalLocations().get(0).getChromosome());

        HSSFCell proteomicsCell = row.createCell(4);
        proteomicsCell.setCellValue(entryName);

        HSSFCell diseaseCell = row.createCell(5);
        diseaseCell.setCellValue(entryName);

        HSSFCell structureCell = row.createCell(6);
        structureCell.setCellValue(entryName);

        HSSFCell isoformCountCell = row.createCell(7);
        isoformCountCell.setCellValue(entryName);

        HSSFCell variantCountCell = row.createCell(8);
        variantCountCell.setCellValue(entryName);

        HSSFCell ptmCountCell = row.createCell(9);
        ptmCountCell.setCellValue(entryName);

        HSSFCell mutagenesisCell = row.createCell(10);
        mutagenesisCell.setCellValue(entryName);

        HSSFCell tissueExpressionCell = row.createCell(11);
        tissueExpressionCell.setCellValue(entryName);

        HSSFCell proteinEvidenceCell = row.createCell(12);
        proteinEvidenceCell.setCellValue(entryName);

        rowIndex++;
    }

    @Override
    protected void writeFooter() throws IOException {

        workbook.write(os);
    }
}
