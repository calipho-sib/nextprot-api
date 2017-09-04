package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Export entry annotation informations as defined by Header
 */
interface EntryPartExporter {

    List<Header> exportHeaders();
    List<Row> exportRows(Entry entry);
    int getColumnIndex(Header header);

    enum Header {

        ENTRY_ACCESSION,
        CATEGORY,
        TERM_ACCESSION,
        TERM_NAME,
        QUALITY,
        ECO_ACCESSION,
        ECO_NAME,
        NEGATIVE,
        EXPRESSION_LEVEL,
        TISSUE_ACCESSION,
        TISSUE_NAME,
        STAGE_ACCESSION,
        STAGE_NAME,
        CELL_LINE_ACCESSION,
        CELL_LINE_NAME,
        DISEASE_ACCESSION,
        DISEASE_NAME,
        ORGANELLE_ACCESSION,
        ORGANELLE_NAME,
        SOURCE,
        URL
    }

    class Row {

        private final String[] row;

        Row(int capacity) {

            this.row = new String[capacity];
        }

        public void setValue(int index, String value) {

            if (index < 0 || index >= row.length) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            row[index] = value;
        }

        public String getValue(int index) {

            return row[index];
        }

        public Stream<String> stream() {
            return Arrays.stream(row);
        }

        public int size() {
            return row.length;
        }
    }
}
