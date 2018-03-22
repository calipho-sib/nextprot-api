package org.nextprot.api.core.utils.exon;

import org.junit.Assert;
import org.junit.Test;

public class ExonsMappingConsistencyTest {

    @Test
    public void checkConsistency() {

        ExonsMappingConsistency consistency = new ExonsMappingConsistency("NX_P78324");
        ExonsMappingConsistency.ConsistencyResult consistencyResult = consistency.check("ENSG00000198053");

        Assert.assertTrue(consistencyResult.isConsistent());
    }
}




