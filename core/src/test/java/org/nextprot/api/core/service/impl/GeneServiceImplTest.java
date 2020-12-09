package org.nextprot.api.core.service.impl;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.impl.MasterIdentifierServiceImpl.MapStatus;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev","cache" })
public class GeneServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private GeneService geneService;

    @Autowired
    private GeneIdentifierService geneIdentifierService;
    
    @Autowired
    private MasterIdentifierService masterIdentifierService;
    
    

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
    
    
    @Ignore
    @Test
    public void classifyGeneMapping() throws Exception {
    	String filename = "/Users/pmichel/tmp/bgee/ensg.list";
    	String file_out = "/Users/pmichel/tmp/bgee/ensg.out";   	
    	// fake call to load cache
    	long t0 = System.currentTimeMillis();
    	masterIdentifierService.getMapStatusForENSG("Schtroumpf");
    	System.out.println("Took " + (System.currentTimeMillis()-t0) + " ms to load cache") ;    	
    	BufferedReader reader = new BufferedReader(new FileReader(filename));
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file_out));
	    String ensg = null;
        int line = 0;
        t0 = System.currentTimeMillis();
        while ((ensg = reader.readLine()) != null) {
            line++;
            if (line % 10000 == 0) System.out.println("Processing line " + line);
            MapStatus s = masterIdentifierService.getMapStatusForENSG(ensg);
            String result = ensg + "\t" + s.getStatus() + "\t" + s.getEntries().stream().collect(Collectors.joining(",")) + "\n";
            writer.write(result);
        }
        System.out.println("Processing line " + line);
        System.out.println("Took " + (System.currentTimeMillis()-t0) + " ms.") ;
        System.out.println("end");        
        reader.close();
        writer.close();
    	
    }
    
}