package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

import java.util.List;

public interface EntryPartExporter {

    List<Header> exportHeaders();
    List<Row> exportRows(Entry entry);
}
