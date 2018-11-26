package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.export.format.EntryBlock;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Export informations on entries overview in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public class EntryOverviewXLSWriter extends EntryXLSWriter {

    private static class DataProvider implements EntryDataProvider {

        private final EntryReportStatsService entryReportStatsService;

        DataProvider() {
            entryReportStatsService = SpringApplicationContext.getBeanOfType(EntryReportStatsService.class);
        }

        @Override
        public List<EntryBlock> getSourceEntryBlocks() {
            return Arrays.asList(EntryBlock.OVERVIEW, EntryBlock.CHROMOSOMAL_LOCATION);
        }

        @Override
        public String[] getFieldNames() {
            return new String[]{"acc. code", "protein name", "gene name(s)", "chromosome", "proteomics", "disease", "structure", "#isof.", "#variants", "#PTMS", "mutagenesis", "tissue expr.", "PE"};
        }

        @Override
        public List<Record> getRecords(Entry entry) {

            EntryReportStats entryReport = entryReportStatsService.reportEntryStats(entry.getUniqueName());

            Object[] values = new Object[getFieldNames().length];

            values[0] = entry.getUniqueName();
            values[1] = entry.getOverview().getMainProteinName();
            values[2] = EntityName.toString(entry.getOverview().getGeneNames());
            values[3] = ChromosomalLocation.toString(entry.getChromosomalLocations());
            values[4] = booleanToYesNoString(entryReport.isProteomics());
            values[5] = booleanToYesNoString(entry.getProperties().getFilterdisease());
            values[6] = booleanToYesNoString(entry.getProperties().getFilterstructure());
            values[7] = entryReport.countIsoforms();
            values[8] = entryReport.countVariants();
            values[9] = entryReport.countPTMs();
            values[10] = booleanToYesNoString(entryReport.isMutagenesis());
            values[11] = booleanToYesNoString(entry.getProperties().getFilterexpressionprofile());
            values[12] = entry.getOverview().getProteinExistences().getProteinExistence().getDescription();

            Record record = new Record(values);

            record.setStringValueIndices(new int[]{0, 1, 2, 3, 4, 5, 6, 10, 11, 12});
            record.setIntValueIndices(new int[]{7, 8, 9});

            record.addHyperLinks(0, "http://www.nextprot.org/db/entry/" + entry.getUniqueName());

            return Arrays.asList(record);
        }

        private final String booleanToYesNoString(boolean bool) {

            return (bool) ? "yes" : "no";
        }
    }

    public EntryOverviewXLSWriter(OutputStream stream) {

        super(stream, "Proteins", new DataProvider());
    }
}
