package org.nextprot.api.core.service.export.writer;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.export.writer.ChromosomeReportTSVWriter.extractValues;

/**
 * Writes a {@code ChromosomeReport} in TXT format
 *
 * Created by fnikitin on 19.04.17.
 */
public class ChromosomeReportTXTWriter extends BaseChromosomeReportWriter {

    private final PrintWriter writer;

    public ChromosomeReportTXTWriter(OutputStream os) {

        super(os);
        this.writer = new PrintWriter(os);
    }

    @Override
    protected void writeHeader(ChromosomeReport.Summary summary) throws IOException {

        writer.write("----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n\n");

        writer.write("Description: Chromosome " + summary.getChromosome() + " report\n");
        writer.write("Name:        nextprot_chromosome_" + summary.getChromosome() + "\n");
        writer.write("Release:     "+ summary.getDataRelease() + "\n");
        writer.write("\n----------------------------------------------------------------------------\n\n");

        writer.write("This file lists all neXtProt entries on chromosome Y\n");
        writer.write("Total number of entries: " + summary.getEntryCount()+ "\n");
        writer.write("Total number of genes: " + summary.getGeneCount()+ "\n");
    }

    @Override
    protected void writeFooter() throws IOException {

        writer.write("____________________________________________________________________________\n" +
                "\n" +
                "Copyrighted by the SIB Swiss Institute of Bioinformatics and Geneva\n" +
                "Bioinformatics (GeneBio) SA\n" +
                "\n" +
                "Distributed under the Creative Commons Attribution-NoDerivs License\n" +
                "----------------------------------------------------------------------------\n");
    }

    @Override
    protected void writeChromosomeReport(ChromosomeReport report) throws IOException {

        writer.write("\n--------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        writer.write(String.format(buildHeaderFormat(),
                Arrays.asList("Gene ", "neXtProt", "Chromosomal", "Start", "Stop", "Protein", "Prote-", "Anti-", "3D", "Dise-", "Iso-", "Vari-", "PTMs", "Description").toArray()));
        writer.write(String.format(buildHeaderFormat(),
                Arrays.asList("name ", "AC", "position", "position", "position", "existence", "omics", "body", "", "ase", "forms", "ants", "", "").toArray()));
        writer.write("________________________________________________________________________________________________________________________________________________________\n");

        for (EntryReport er : report.getEntryReports()) {

            writeEntryReport(extractValues(er));
        }
    }

    @Override
    public void close() throws IOException {

        writer.close();
        super.close();
    }

    private void writeEntryReport(List<String> values) throws IOException {

        writer.write(String.format(buildRowFormat(), values.toArray()));
    }

    private static String buildHeaderFormat() {

        return "%-10s%-13s%-13s%-9s%-9s%-16s %-6s %-6s%-6s%-6s%-6s%-7s%-5s%s%n";
    }

    private static String buildRowFormat() {

        return "%-10s%-13s%-13s%8s%9s %-16s %-6s %-6s%-6s%-5s%6s%6s%6s %s%n";
    }
}
