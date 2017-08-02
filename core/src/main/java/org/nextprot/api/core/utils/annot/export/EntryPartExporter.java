package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

import java.util.List;

public interface EntryPartExporter {

    enum Header {
        ENTRY_ACCESSION,
        TERM_ACCESSION,
        TERM_NAME,
        ANNOTATION_QUALITY,
        ECO_ACCESSION,
        ECO_NAME,
        EVIDENCE_ASSIGNED_BY,
        EVIDENCE_QUALITY,
        EXPRESSION_LEVEL,
        STAGE_ACCESSION,
        STAGE_NAME,
        CELL_LINE_ACCESSION,
        CELL_LINE_NAME,
        DISEASE_ACCESSION,
        DISEASE_NAME,
        ORGANELLE_ACCESSION,
        ORGANELLE_NAME,
    }

    List<Header> exportHeaders();
    List<List<String>> getRows(Entry entry);
}
