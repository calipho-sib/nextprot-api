package org.nextprot.api.blast.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.blast.domain.BlastSearchParams;
import org.nextprot.api.blast.domain.BlastSequenceInput;
import org.nextprot.api.blast.domain.gen.Report;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class BlastPRunnerTest {

    @Value("${blastp.bin}")
    private String blastBinPath;

    @Value("${blastp.db}")
    private String blastDb;

    private BlastSequenceInput config;

    @Autowired
    private BlastResultUpdaterService updater;

    @Test
    public void blastpShouldFindResult() throws Exception {

        config = new BlastSequenceInput(blastBinPath, blastDb);

        BlastPRunner runner = new BlastPRunner(config);

        Report blastReport = runner.run(new BlastPRunner.FastaEntry("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"));

        Assert.assertNull(config.getBinPath());
        Assert.assertNull(config.getNextprotBlastDbPath());

        Assert.assertEquals(4, blastReport.getResults().getSearch().getHits().size());
    }

    @Test(expected = NextProtException.class)
    public void blastpShouldThrowNPException() throws Exception {

        config = new BlastSequenceInput("/work/devtools/blastw", blastDb);

        BlastPRunner runner = new BlastPRunner(config);

        runner.run(new BlastPRunner.FastaEntry("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"));
    }

    @Test
    public void testDefaultCommandLineBuilding() throws Exception {

        config = new BlastSequenceInput(blastBinPath, blastDb);

        BlastPRunner runner = new BlastPRunner(config);

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(config, file);

        Assert.assertNotNull(config.getBinPath());
        Assert.assertNotNull(config.getNextprotBlastDbPath());

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

        config = new BlastSequenceInput(blastBinPath, blastDb);
        config.setBlastSearchParams(BlastSearchParams.valueOf(BlastSearchParams.Matrix.BLOSUM45.toString(), 0.01, 12, 2));

        BlastPRunner runner = new BlastPRunner(config);

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(config, file);

        Assert.assertNotNull(config.getBinPath());
        Assert.assertNotNull(config.getNextprotBlastDbPath());

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

    @Test(expected = NextProtException.class)
    public void shouldNotBeAbleToCreateInstance() throws Exception {

        config = new BlastSequenceInput(null, "/tmp/blastdb");

        new BlastPRunner(config);
    }

    @Test(expected = ExceptionWithReason.class)
    public void blastpShouldThrowExceptionWithReason() throws Exception {

        config = new BlastSequenceInput(blastBinPath, blastDb);
        // pam30, gapopen11
        config.setBlastSearchParams(BlastSearchParams.valueOf(BlastSearchParams.Matrix.PAM30.toString(), 0.01, 11, 2));

        BlastPRunner runner = new BlastPRunner(config);
        runner.run(new BlastPRunner.FastaEntry("subseq 211-239 of NX_P52701", "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR"));
    }
}