package org.nextprot.api.blast.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.blast.domain.BlastConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class BlastDbMakerTest {

    @Value("${makeblastdb.bin}")
    private String makeblastdbBinPath;

    private BlastConfig config;

    @Test
    public void testCommandLineBuilding() throws Exception {

        config = new BlastConfig("/tmp/blastdb");
        config.setMakeBlastDbBinPath(makeblastdbBinPath);

        BlastDbMaker runner = new BlastDbMaker(config);

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(file);

        Assert.assertEquals(Arrays.asList(makeblastdbBinPath, "-dbtype", "prot", "-title",
                "nextprot", "-in", "/tmp/input.fasta", "-out", "/tmp/blastdb"), cl);
    }

    @Test
    public void shouldCreateDb() throws Exception {

        config = new BlastConfig("/tmp/blastdb");
        config.setMakeBlastDbBinPath(makeblastdbBinPath);

        BlastDbMaker runner = new BlastDbMaker(config);

        String result = runner.run("> subseq 211-239 of NX_P52701\nGTTYVTDKSEEDNEIESEEEVQPKTQGSRR");

        Assert.assertTrue(result.contains("Building a new DB, current time:"));
        Assert.assertTrue(result.contains("New DB name:   /tmp/blastdb"));
        Assert.assertTrue(result.contains("New DB title:  nextprot"));
        Assert.assertTrue(result.contains("Sequence type: Protein"));
        Assert.assertTrue(result.contains("Adding sequences from FASTA; added 1 sequences"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToCreateInstance() throws Exception {

        config = new BlastConfig("/tmp/blastdb");

        new BlastDbMaker(config);
    }
}