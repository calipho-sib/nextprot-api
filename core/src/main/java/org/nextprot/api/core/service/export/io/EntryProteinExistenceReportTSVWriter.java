package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.ProteinExistences;
import org.nextprot.api.core.service.export.EntryProteinExistenceReportWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes {@code ProteinExistences} in TSV format
 */
public class EntryProteinExistenceReportTSVWriter implements EntryProteinExistenceReportWriter {

    private final PrintWriter writer;

    public EntryProteinExistenceReportTSVWriter(OutputStream os) {

        this.writer = new PrintWriter(os);

        writer.write(Stream.of("neXtProt AC", "UniProt", "Inferred from NP1", "Inferred from NP2", "NP2 Inferring Rule")
                .collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    @Override
    public void write(String entryAccession, ProteinExistences proteinExistences) {

        writer.write(
                Stream.of(
                        entryAccession,
                        proteinExistences.getOtherProteinExistenceUniprot().getDescription(),
                        proteinExistences.getOtherProteinExistenceNexprot1().getDescription(),
                        proteinExistences.getInferredProteinExistence().getDescription(),
                        (proteinExistences.getProteinExistenceInferred().isInferenceFound()) ? proteinExistences.getProteinExistenceInferred().getRule().getTitle() : "No matching rule"
                ).collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    @Override
    public void close() {

        writer.close();
    }
}
