package org.nextprot.api.blast.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.blast.domain.BlastConfig;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class BlastRunnerTest {

    @Value("${blastp.bin}")
    private String blastBinPath;

    @Value("${blastp.db}")
    private String blastDb;

    private BlastConfig config;

    @Test
    public void blastpShouldFindResult() throws Exception {

        config = new BlastConfig(blastDb);
        config.setBlastBinPath(blastBinPath);

        BlastRunner runner = new BlastRunner(config);

        BlastResult blastResult = runner.run("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR");

        Assert.assertEquals(1, blastResult.getBlastOutput2().size());
    }

    @Test(expected = NextProtException.class)
    public void blastpShouldThrowNPException() throws Exception {

        config = new BlastConfig(blastDb);
        config.setBlastBinPath("/work/devtools/blastw");

        BlastRunner runner = new BlastRunner(config);

        runner.run("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR");
    }

    @Test
    public void testDefaultCommandLineBuilding() throws Exception {

        config = new BlastConfig(blastDb);
        config.setBlastBinPath(blastBinPath);

        BlastRunner runner = new BlastRunner(config);

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

    @Test
    public void testCommandLineBuildingWithParams() throws Exception {

        config = new BlastConfig(blastDb);
        config.setBlastBinPath(blastBinPath);
        config.setEvalue(0.01);
        config.setMatrix(BlastConfig.Matrix.BLOSUM45);
        config.setGapOpen(12);
        config.setGapExtend(2);

        BlastRunner runner = new BlastRunner(config);

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(file);

        Assert.assertEquals(15, cl.size());
        Assert.assertTrue(cl.get(0).endsWith("blastp"));
        Assert.assertEquals("-db", cl.get(1));
        Assert.assertTrue(cl.get(2).endsWith("nextprot"));
        Assert.assertEquals("-query", cl.get(3));
        Assert.assertEquals(file.getAbsolutePath(), cl.get(4));
        Assert.assertEquals("-outfmt", cl.get(5));
        Assert.assertEquals("15", cl.get(6));
        Assert.assertEquals("-matrix", cl.get(7));
        Assert.assertEquals("BLOSUM45", cl.get(8));
        Assert.assertEquals("-evalue", cl.get(9));
        Assert.assertEquals("0.01", cl.get(10));
        Assert.assertEquals("-gapopen", cl.get(11));
        Assert.assertEquals("12", cl.get(12));
        Assert.assertEquals("-gapextend", cl.get(13));
        Assert.assertEquals("2", cl.get(14));
    }
}