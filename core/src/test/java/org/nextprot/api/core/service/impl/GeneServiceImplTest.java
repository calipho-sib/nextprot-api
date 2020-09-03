package org.nextprot.api.core.service.impl;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

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
    
    @Test
    public void checkEntryEnsgMap() {
    	
    	Map<String,List<String>> map = geneService.getEntryENSGMap();
    	
    	// we have more than 20'000 protein entries and more than 20'000 ENSG accessions
    	Assert.assertTrue(map.size() > 40000);
    	
    	// some one to one relationship between entry and ensg should exist
    	Assert.assertTrue(map.get("NX_Q8N816").contains("ENSG00000167920"));
    	
    	// symmetric relationship between same ensg and entry should exist as well
    	Assert.assertTrue(map.get("ENSG00000167920").contains("NX_Q8N816"));
    	
    	// we should encounter some multi-gene entry
    	Assert.assertTrue(map.get("NX_Q0WX57").size() > 1);

    	// we should encounter some multi-entry gene
    	Assert.assertTrue(map.get("ENSG00000225830").size() > 1);
    	
    }
    
}