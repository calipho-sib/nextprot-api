package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    protected void writeHeader(ChromosomeReport report) throws IOException {

        writer.write("----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n\n");

        writer.write("Description: Chromosome " + report.getSummary().getChromosome() + " report\n");
        writer.write("Name:        nextprot_chromosome_" + report.getSummary().getChromosome() + "\n");
        writer.write("Release:     "+ report.getDataRelease() + "\n");
        writer.write("\n----------------------------------------------------------------------------\n\n");

        writer.write("This file lists all neXtProt entries on chromosome Y\n");
        writer.write("Total number of entries: " + report.getSummary().getEntryCount()+ "\n");
        writer.write("Total number of genes: " + report.getSummary().getEntryReportCount()+ "\n");
    }

    @Override
    protected void writeFooter() throws IOException {

        writer.write("____________________________________________________________________________\n" +
                "\n" +
                "Copyrighted by the SIB Swiss Institute of Bioinformatics\n" +
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
                Arrays.asList("name ", "AC", "location", "position", "position", "existence", "omics", "body", "", "ase", "forms", "ants", "", "").toArray()));
        writer.write("________________________________________________________________________________________________________________________________________________________\n");

        for (EntryReport er : report.getEntryReports()) {

            List<String> allValuesExceptCodingStrand = new ArrayList<>(extractValues(er));
            allValuesExceptCodingStrand.remove(5);
            writer.write(String.format(buildRowFormat(), allValuesExceptCodingStrand.toArray()));
        }
    }

    @Override
    public void close() throws IOException {

        writer.close();
        super.close();
    }


    private static String buildHeaderFormat() {

        return "%-14s%-13s %-13s%-10s %-10s %-16s %-6s %-6s%-6s%-6s%-6s%-7s%-5s%s%n";
    }

    private static String buildRowFormat() {

        return "%-14s%-13s %-13s%10s %10s %-16s %-6s %-6s%-6s%-5s%6s%6s%6s %s%n";
    }
}
