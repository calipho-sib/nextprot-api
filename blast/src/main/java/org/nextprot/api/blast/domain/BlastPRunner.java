package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.blast.domain.gen.*;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.OverviewService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hp.hpl.jena.sparql.vocabulary.VocabTestQuery.query;

/**
 * Executes locally installed blastP program with protein sequence query
 */
public class BlastPRunner {

    private File tempQueryFile;
    private final BlastPConfig config;

    public BlastPRunner(BlastPConfig config) {

        this.config = config;
    }

    public BlastResult run(String header, String sequence) throws NextProtException {

        return run(header, sequence, null);
    }

    public BlastResult run(String header, String sequence, OverviewService overviewService) throws NextProtException {

        try {
            StringBuilder fasta = new StringBuilder();
            fasta.append(">").append(header).append("\n").append(sequence);

            writeTempQueryFile(fasta.toString());

            SystemCommandExecutor commandExecutor = new SystemCommandExecutor(buildCommandLine());
            commandExecutor.executeCommand();

            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            if (!stderr.toString().isEmpty()) {
                throw new NextProtException("BlastP error when executing " + query + ": " + stderr.toString());
            }

            if (!config.isDebugMode()) {
                deleteTempQueryFile();
            }

            BlastResult blastResult = BlastResult.fromJson(stdout.toString());

            if (overviewService == null)
                return blastResult;

            return new BlastOutputUpdater(overviewService, sequence).update(blastResult);
        } catch (IOException | InterruptedException e) {

            throw new NextProtException("BlastP exception", e);
        }
    }

    List<String> buildCommandLine() {

        List<String> command = new ArrayList<>();

        command.add(config.getBlastDirPath() + "/blastp");
        command.add("-db");
        command.add(config.getNextprotDatabasePath() + "/nextprot");
        command.add("-query");
        command.add(tempQueryFile.getAbsolutePath());
        command.add("-outfmt");
        command.add("15");

        return command;
    }

    private void writeTempQueryFile(String sequenceQuery) throws IOException {

        tempQueryFile = File.createTempFile("blastp_query", "fasta");

        PrintWriter pw = new PrintWriter(tempQueryFile);
        pw.write(sequenceQuery);
        pw.close();
    }

    private void deleteTempQueryFile() throws IOException {

        Files.deleteIfExists(tempQueryFile.toPath());
    }

    /**
     * Edit original blast output:
     *
     * 1. Remove useless fields (ex: blast paper reference)
     * 2. Add some new fields (ex: sequence query, matching isoform accession,...).
     */
    private class BlastOutputUpdater {

        private final OverviewService overviewService;
        private final String sequence;

        private BlastOutputUpdater(OverviewService overviewService, String sequence) {

            this.overviewService = overviewService;
            this.sequence = sequence;
        }

        private BlastResult update(BlastResult originalBlastResult) {

            Report report = originalBlastResult.getBlastOutput2().get(0).getReport();

            // remove useless informations
            report.setProgram(null);
            report.setReference(null);
            report.setSearchTarget(null);

            Search search = report.getResults().getSearch();

            search.setQuerySeq(sequence);

            Pattern titlePattern = Pattern.compile("^.+\\s+(NX_[^|]+)\\|(NX_.+)$");

            // HITs
            for (Hit hit : search.getHits()) {

                // DESCRIPTIONs
                for (Description desc : hit.getDescription()) {

                    // remove useless infos
                    desc.setId(null);
                    desc.setAccession(null);

                    String old = desc.getTitle();
                    Matcher matcher = titlePattern.matcher(old);
                    if (matcher.find()) {

                        String isoformAccession = matcher.group(1);
                        String entryAccession = matcher.group(2);

                        Overview overview = overviewService.findOverviewByEntry(entryAccession);

                        desc.setTitle(overview.getMainProteinName()+" ("+overview.getMainGeneName()+") ["+isoformAccession+"]");
                        desc.setEntryAccession(entryAccession);
                        desc.setIsoAccession(isoformAccession);
                        desc.setProteinName(overview.getMainProteinName());
                        desc.setGeneName(overview.getMainGeneName());
                    }
                }

                for (Hsp hsp : hit.getHsps()) {

                    float identityPercent = (float)hsp.getIdentity()/search.getQueryLen()*100;

                    hsp.setIdentityPercent(Float.parseFloat(new DecimalFormat("##.##").format(identityPercent)));
                }
            }

            return originalBlastResult;
        }
    }
}
