package org.nextprot.api.core.service.impl;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class GeneServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private GeneService geneService;

    @Autowired
    private GeneIdentifierService geneIdentifierService;

    @Test
    public void findGeneNames() {

        Assert.assertEquals(Lists.newArrayList("SCN11A", "SCN12A", "SNS2"), geneIdentifierService.findGeneNamesByEntryAccession("NX_Q9UI33"));

    }

    @Test
    public void checkGeneNames() {

        Assert.assertTrue(geneService.isValidGeneName("NX_Q9UI33", "SCN11A"));
        Assert.assertTrue(geneService.isValidGeneName("NX_Q9UI33", "SCN12A"));
        Assert.assertTrue(geneService.isValidGeneName("NX_Q9UI33", "SNS2"));
    }
}