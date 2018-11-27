package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.springframework.context.ApplicationContext;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Export informations on entries isoforms in XLS format
 *
 * Created by fnikitin on 11/08/15.
 */
public class EntryIsoformXLSWriter extends EntryXLSWriter {

    private static class DataProvider implements EntryDataProvider {

        @Override
        public List<EntryBlock> getSourceEntryBlocks() {
            return Arrays.asList(EntryBlock.OVERVIEW, EntryBlock.ISOFORM);
        }

        @Override
        public String[] getFieldNames() {
            return new String[] {"acc. code", "protein name", "isoform", "seq. len", "mass", "PI"};
        }

        @Override
        public List<Record> getRecords(Entry entry) {

            List<Record> records = new ArrayList<>();

            for (Isoform isoform : entry.getIsoforms()) {

                Object[] values = new Object[getFieldNames().length];

                values[0] = isoform.getUniqueName();
                values[1] = entry.getOverview().getMainProteinName();
                values[2] = isoform.getMainEntityName().getValue();
                values[3] = isoform.getSequenceLength();
                values[4] = (int)Math.round(DescriptorMass.compute(isoform.getSequence()));
                values[5] = DescriptorPI.compute(isoform.getSequence());

                Record record = new Record(values);

                record.setStringValueIndices(new int[] { 0, 1, 2 });
                record.setIntValueIndices(new int[] { 3, 4 });
                record.setDoubleValueIndices(new int[] { 5 });

                record.addHyperLinks(0, "http://www.nextprot.org/db/entry/"+entry.getUniqueName()+"/sequence?isoforms=" + isoform.getUniqueName());

                records.add(record);
            }

            return records;
        }
    }

    public EntryIsoformXLSWriter(OutputStream stream, ApplicationContext applicationContext) {

        super(stream, "Isoforms", new DataProvider(), applicationContext);
    }
}
