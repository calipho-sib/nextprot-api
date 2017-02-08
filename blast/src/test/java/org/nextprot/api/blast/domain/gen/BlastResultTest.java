package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class BlastResultTest {

    @Test
    public void fromJson() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        BlastResult blastResult = mapper.readValue(new File(BlastResultTest.class.getResource("blastresult.json").getFile()), BlastResult.class);

        Assert.assertEquals(1, blastResult.getBlastOutput2().size());
    }
}