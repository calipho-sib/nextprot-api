package org.nextprot.api.blast.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.util.List;

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

    @Test
    public void testDefaultCommandLineBuilding() throws Exception {

        BlastPRunner runner = new BlastPRunner(new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db"));

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(file);

        Assert.assertEquals(7, cl.size());
        Assert.assertTrue(cl.get(0).endsWith("blastp"));
        Assert.assertEquals("-db", cl.get(1));
        Assert.assertTrue(cl.get(2).endsWith("nextprot"));
        Assert.assertEquals("-query", cl.get(3));
        Assert.assertEquals(file.getAbsolutePath(), cl.get(4));
        Assert.assertEquals("-outfmt", cl.get(5));
        Assert.assertEquals("15", cl.get(6));
    }
}