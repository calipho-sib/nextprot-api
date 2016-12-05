package org.nextprot.api.blast.domain.gen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BlastResultTest {

    @Test
    public void fromJson() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        BlastResult blastResult = mapper.readValue(new File(BlastResultTest.class.getResource("blastresult.json").getFile()), BlastResult.class);

        Assert.assertEquals(1, blastResult.getBlastOutput2().size());
    }

    @Test
    public void writeJson() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        BlastResult obj = new BlastResult();

        //Object to JSON in file
        mapper.writeValue(new File("file.json"), obj);




    }
}