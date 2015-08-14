package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.EntryBlock;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Export informations on entries overview in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public class NPEntryOverviewXLSWriter extends NPEntryXLSWriter {

    private static class Strategy implements WritingRowStrategy {

        @Override
        public List<EntryBlock> getSourceEntryBlocks() {
            return Arrays.asList(EntryBlock.OVERVIEW, EntryBlock.CHROMOSOMAL_LOCATION);
        }

        @Override
        public String[] getHeaders() {
            return new String[] { "acc. code", "protein name", "gene name(s)", "chromosome", "proteomics", "disease",	"structure", "#isof.", "#variants", "#PTMS", "mutagenesis", "tissue expr.", "PE" };
        }

        @Override
        public List<Record> getRecords(Entry entry) {

            Object[] values = new Object[getHeaders().length];

            values[0] = entry.getUniqueName();
            values[1] = entry.getOverview().getMainProteinName();
            values[2] = entry.getOverview().getMainGeneName();
            ChromosomalLocation location = entry.getChromosomalLocations().get(0);
            values[3] = location.getChromosome() + location.getBand();
            values[4] = booleanToYesNoString(entry.getProperties().getFilterproteomics());
            values[5] = booleanToYesNoString(entry.getProperties().getFilterdisease());
            values[6] = booleanToYesNoString(entry.getProperties().getFilterstructure());
            values[7] = entry.getProperties().getIsoformCount();
            values[8] = entry.getProperties().getVarCount();
            values[9] = entry.getProperties().getPtmCount();
            values[10] = booleanToYesNoString(entry.getProperties().getFiltermutagenesis());
            values[11] = booleanToYesNoString(entry.getProperties().getFilterexpressionprofile());
            values[12] = entry.getProperties().getProteinExistence();

            Record record = new Record();

            record.setValues(values);
            record.setStringValueIndices(new int[]{0, 1, 2, 3, 4, 5, 6, 10, 11, 12});
            record.setIntValueIndices(new int[]{7, 8, 9});

            record.addHyperLinks(0, "http://www.nextprot.org/db/entry/" + entry.getUniqueName());

            return Arrays.asList(record);
        }

        private final String booleanToYesNoString(boolean bool) {

            return (bool) ? "yes" : "no";
        }
    }

    public NPEntryOverviewXLSWriter(OutputStream stream) {

        super(stream, "Proteins", new Strategy());
    }
}
