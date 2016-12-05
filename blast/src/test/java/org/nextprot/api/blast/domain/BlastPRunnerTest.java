package org.nextprot.api.blast.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.commons.exception.NextProtException;

public class BlastPRunnerTest {

    @Test
    public void blastpShouldFindResult() throws Exception {

        BlastPRunner runner = new BlastPRunner(new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db"));

        BlastResult blastResult = runner.run("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR");

        Assert.assertEquals(1, blastResult.getBlastOutput2().size());
    }

    @Test(expected = NextProtException.class)
    public void blastpShouldThrowNPException() throws Exception {

        BlastPRunner runner = new BlastPRunner(new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+", "/Users/fnikitin/data/blast/db"));

        runner.run("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR");
    }
}