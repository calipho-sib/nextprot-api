package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ProteinExistences;

public interface EntryProteinExistenceReportWriter {

    void write(String entryAccession, ProteinExistences proteinExistences);
    void close();
}
