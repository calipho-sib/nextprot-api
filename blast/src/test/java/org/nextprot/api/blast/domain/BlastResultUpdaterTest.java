package org.nextprot.api.blast.domain;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;

import java.util.Collections;

import static org.mockito.Matchers.any;

public class BlastResultUpdaterTest {

    @Test
    public void updateShouldSetSomeFieldsToNull() throws Exception {

        BlastResult blastResult = runBlast();

        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getReference());
        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getStat().getEntropy());

        BlastResultUpdater updater = new BlastResultUpdater(mockMainNamesService(), "LTARKGAEDSAEDLGGPCPEPGGDSGVLGANGASCSRGEAEEPAGRRRARPVRSKARR");
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

        BlastResultUpdater updater = new BlastResultUpdater(mockMainNamesService(), "LTARKGAEDSAEDLGGPCPEPGGDSGVLGANGASCSRGEAEEPAGRRRARPVRSKARR");
        updater.update(blastResult);

        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getQuerySeq());
        Assert.assertNotNull(blastResult.getBlastOutput2().get(0).getReport().getResults().getSearch().getHits().get(0).getHsps().get(0).getIdentityPercent());
    }

    private static BlastResult runBlast() {

        return new BlastRunner(new BlastConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db")).run("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR");
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