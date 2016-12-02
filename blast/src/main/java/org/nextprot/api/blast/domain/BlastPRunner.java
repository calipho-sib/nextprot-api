package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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

    public String run(String header, String sequence) throws NextProtException {

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

            return stdout.toString();
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

    /*
{
  "BlastOutput2": [
    {
      "report": {
        "program": "blastp",
*       "version": "BLASTP 2.3.0+",
        "reference": "Stephen F. Altschul, Thomas L. Madden, Alejandro A. Sch&auml;ffer, Jinghui Zhang, Zheng Zhang, Webb Miller, and David J. Lipman (1997), \"Gapped BLAST and PSI-BLAST: a new generation of protein database search programs\", Nucleic Acids Res. 25:3389-3402.",
        "search_target": {
          "db": "/Users/fnikitin/data/blast/db/nextprot"
        },
        "params": {
*         "matrix": "BLOSUM62",
          "expect": 10,
          "gap_open": 11,
          "gap_extend": 1,
          "filter": "F",
          "cbs": 2
        },
        "results": {
          "search": {
            "query_id": "Query_1",
            "query_title": "unnamed protein product",
            "query_len": 30,
            "hits": [
              {
                "num": 1,
                "description": [
                  {
                    "id": "gnl|BL_ORD_ID|9281",
                    "accession": "9281",
                    "title": "2430223|637565 NX_P52701-3|NX_P52701"
                  }
                ],
                "len": 1230,
                "hsps": [
                  {
                    "num": 1,
                    "bit_score": 61.6178,
                    "score": 148,
                    "evalue": 1.45816e-12,
                    "identity": 30,
                    "positive": 30,
                    "query_from": 1,
                    "query_to": 30,
                    "hit_from": 81,
                    "hit_to": 110,
                    "align_len": 30,
                    "gaps": 0,
                    "qseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "hseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "midline": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"
                  }
                ]
              },
              {
                "num": 2,
                "description": [
                  {
                    "id": "gnl|BL_ORD_ID|9279",
                    "accession": "9279",
                    "title": "637566|637565 NX_P52701-1|NX_P52701"
                  }
                ],
                "len": 1360,
                "hsps": [
                  {
                    "num": 1,
                    "bit_score": 61.6178,
                    "score": 148,
                    "evalue": 1.55294e-12,
                    "identity": 30,
                    "positive": 30,
                    "query_from": 1,
                    "query_to": 30,
                    "hit_from": 211,
                    "hit_to": 240,
                    "align_len": 30,
                    "gaps": 0,
                    "qseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "hseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "midline": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"
                  }
                ]
              },
              {
                "num": 3,
                "description": [
                  {
                    "id": "gnl|BL_ORD_ID|9280",
                    "accession": "9280",
                    "title": "637567|637565 NX_P52701-2|NX_P52701"
                  }
                ],
                "len": 1068,
                "hsps": [
                  {
                    "num": 1,
                    "bit_score": 61.2326,
                    "score": 147,
                    "evalue": 1.68076e-12,
                    "identity": 30,
                    "positive": 30,
                    "query_from": 1,
                    "query_to": 30,
                    "hit_from": 211,
                    "hit_to": 240,
                    "align_len": 30,
                    "gaps": 0,
                    "qseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "hseq": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR",
                    "midline": "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"
                  }
                ]
              },
              {
                "num": 4,
                "description": [
                  {
                    "id": "gnl|BL_ORD_ID|5692",
                    "accession": "5692",
                    "title": "612083|612082 NX_Q8N2Z9-1|NX_Q8N2Z9"
                  }
                ],
                "len": 138,
                "hsps": [
                  {
                    "num": 1,
                    "bit_score": 24.2534,
                    "score": 51,
                    "evalue": 3.94188,
                    "identity": 11,
                    "positive": 14,
                    "query_from": 4,
                    "query_to": 24,
                    "hit_from": 95,
                    "hit_to": 115,
                    "align_len": 21,
                    "gaps": 0,
                    "qseq": "YVTDKSEEDNEIESEEEVQPK",
                    "hseq": "YITDKSEEIAQINLERKAQKK",
                    "midline": "Y+TDKSEE  +I  E + Q K"
                  }
                ]
              }
            ],
            "stat": {
              "db_num": 42024,
              "db_len": 24262665,
              "hsp_len": 5,
              "eff_space": 601313625,
              "kappa": 0.041,
              "lambda": 0.267,
              "entropy": 0.14
            }
          }
        }
      }
    }
  ]
}
     */
}
