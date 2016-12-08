package org.nextprot.api.blast.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Matchers.any;

public class BlastResultUpdaterTest {

    @Test
    public void updateShouldSetSomeFieldsToNull() throws Exception {

        BlastResult blastResult = runBlast();

        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getReference());
        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getStat().getEntropy());

        BlastResultUpdater updater = new BlastResultUpdater(mockMainNamesService(), "WHATEVER");
        updater.update(blastResult);

        Assert.assertNull(blastResult.getBlastOutput2().get(0).getReport().getReference());
        Assert.assertNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getQueryId());
        Assert.assertNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getStat().getEntropy());
    }

    @Test
    public void updateShouldDefineNewFields() throws Exception {

        BlastResult blastResult = runBlast();

        Assert.assertNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getQuerySeq());
        Assert.assertNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getHits().get(0).getHsps().get(0).getIdentityPercent());

        BlastResultUpdater updater = new BlastResultUpdater(mockMainNamesService(), "WHATEVER");
        updater.update(blastResult);

        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getQuerySeq());
        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getHits().get(0).getHsps().get(0).getIdentityPercent());
    }

    private static BlastResult runBlast() throws IOException {

        return BlastResult.fromJson("{\n" +
                "\"BlastOutput2\": [\n" +
                "{\n" +
                "    \"report\": {\n" +
                "      \"program\": \"blastp\",\n" +
                "      \"version\": \"BLASTP 2.3.0+\",\n" +
                "      \"reference\": \"Stephen F. Altschul, Thomas L. Madden, Alejandro A. Sch&auml;ffer, Jinghui Zhang, Zheng Zhang, Webb Miller, and David J. Lipman (1997), \\\"Gapped BLAST and PSI-BLAST: a new generation of protein database search programs\\\", Nucleic Acids Res. 25:3389-3402.\",\n" +
                "      \"search_target\": {\n" +
                "        \"db\": \"/Users/fnikitin/data/blast/db/nextprot\"\n" +
                "      },\n" +
                "      \"params\": {\n" +
                "        \"matrix\": \"BLOSUM62\",\n" +
                "        \"expect\": 10,\n" +
                "        \"gap_open\": 11,\n" +
                "        \"gap_extend\": 1,\n" +
                "        \"filter\": \"F\",\n" +
                "        \"cbs\": 2\n" +
                "      },\n" +
                "      \"results\": {\n" +
                "        \"search\": {\n" +
                "          \"query_id\": \"Query_1\",\n" +
                "          \"query_title\": \"protein sequence query\",\n" +
                "          \"query_len\": 30,\n" +
                "          \"hits\": [\n" +
                "            {\n" +
                "              \"num\": 1,\n" +
                "              \"description\": [\n" +
                "                {\n" +
                "                  \"id\": \"gnl|BL_ORD_ID|9281\",\n" +
                "                  \"accession\": \"9281\",\n" +
                "                  \"title\": \"2430223|637565 NX_P52701-3|NX_P52701\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"len\": 1230,\n" +
                "              \"hsps\": [\n" +
                "                {\n" +
                "                  \"num\": 1,\n" +
                "                  \"bit_score\": 61.6178,\n" +
                "                  \"score\": 148,\n" +
                "                  \"evalue\": 1.45816e-12,\n" +
                "                  \"identity\": 30,\n" +
                "                  \"positive\": 30,\n" +
                "                  \"query_from\": 1,\n" +
                "                  \"query_to\": 30,\n" +
                "                  \"hit_from\": 81,\n" +
                "                  \"hit_to\": 110,\n" +
                "                  \"align_len\": 30,\n" +
                "                  \"gaps\": 0,\n" +
                "                  \"qseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"hseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"midline\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"num\": 2,\n" +
                "              \"description\": [\n" +
                "                {\n" +
                "                  \"id\": \"gnl|BL_ORD_ID|9279\",\n" +
                "                  \"accession\": \"9279\",\n" +
                "                  \"title\": \"637566|637565 NX_P52701-1|NX_P52701\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"len\": 1360,\n" +
                "              \"hsps\": [\n" +
                "                {\n" +
                "                  \"num\": 1,\n" +
                "                  \"bit_score\": 61.6178,\n" +
                "                  \"score\": 148,\n" +
                "                  \"evalue\": 1.55294e-12,\n" +
                "                  \"identity\": 30,\n" +
                "                  \"positive\": 30,\n" +
                "                  \"query_from\": 1,\n" +
                "                  \"query_to\": 30,\n" +
                "                  \"hit_from\": 211,\n" +
                "                  \"hit_to\": 240,\n" +
                "                  \"align_len\": 30,\n" +
                "                  \"gaps\": 0,\n" +
                "                  \"qseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"hseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"midline\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"num\": 3,\n" +
                "              \"description\": [\n" +
                "                {\n" +
                "                  \"id\": \"gnl|BL_ORD_ID|9280\",\n" +
                "                  \"accession\": \"9280\",\n" +
                "                  \"title\": \"637567|637565 NX_P52701-2|NX_P52701\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"len\": 1068,\n" +
                "              \"hsps\": [\n" +
                "                {\n" +
                "                  \"num\": 1,\n" +
                "                  \"bit_score\": 61.2326,\n" +
                "                  \"score\": 147,\n" +
                "                  \"evalue\": 1.68076e-12,\n" +
                "                  \"identity\": 30,\n" +
                "                  \"positive\": 30,\n" +
                "                  \"query_from\": 1,\n" +
                "                  \"query_to\": 30,\n" +
                "                  \"hit_from\": 211,\n" +
                "                  \"hit_to\": 240,\n" +
                "                  \"align_len\": 30,\n" +
                "                  \"gaps\": 0,\n" +
                "                  \"qseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"hseq\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\",\n" +
                "                  \"midline\": \"GTTYVTDKSEEDNEIESEEEVQPKTQGSRR\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"num\": 4,\n" +
                "              \"description\": [\n" +
                "                {\n" +
                "                  \"id\": \"gnl|BL_ORD_ID|5692\",\n" +
                "                  \"accession\": \"5692\",\n" +
                "                  \"title\": \"612083|612082 NX_Q8N2Z9-1|NX_Q8N2Z9\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"len\": 138,\n" +
                "              \"hsps\": [\n" +
                "                {\n" +
                "                  \"num\": 1,\n" +
                "                  \"bit_score\": 24.2534,\n" +
                "                  \"score\": 51,\n" +
                "                  \"evalue\": 3.94188,\n" +
                "                  \"identity\": 11,\n" +
                "                  \"positive\": 14,\n" +
                "                  \"query_from\": 4,\n" +
                "                  \"query_to\": 24,\n" +
                "                  \"hit_from\": 95,\n" +
                "                  \"hit_to\": 115,\n" +
                "                  \"align_len\": 21,\n" +
                "                  \"gaps\": 0,\n" +
                "                  \"qseq\": \"YVTDKSEEDNEIESEEEVQPK\",\n" +
                "                  \"hseq\": \"YITDKSEEIAQINLERKAQKK\",\n" +
                "                  \"midline\": \"Y+TDKSEE  +I  E + Q K\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ],\n" +
                "          \"stat\": {\n" +
                "            \"db_num\": 42024,\n" +
                "            \"db_len\": 24262665,\n" +
                "            \"hsp_len\": 5,\n" +
                "            \"eff_space\": 601313625,\n" +
                "            \"kappa\": 0.041,\n" +
                "            \"lambda\": 0.267,\n" +
                "            \"entropy\": 0.14\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "]\n" +
                "}\n");
    }

    private static MainNamesService mockMainNamesService() {

        MainNamesService mock = Mockito.mock(MainNamesService.class);

        MainNames mainNames = new MainNames();
        mainNames.setAccession("an accession");
        mainNames.setGeneNameList(Collections.singletonList("a gene name"));
        mainNames.setName("a name");

        Mockito.when(mock.findIsoformOrEntryMainName(any(String.class))).thenReturn(mainNames);

        return mock;
    }
}