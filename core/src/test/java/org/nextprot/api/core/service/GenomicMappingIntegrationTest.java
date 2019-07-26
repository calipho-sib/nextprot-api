package org.nextprot.api.core.service;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev" })
public class GenomicMappingIntegrationTest extends CoreUnitBaseTest {
        
    @Autowired
	private EntryExonMappingService entryExonMappingService;

	@Autowired
	private GenomicMappingService genomicMappingService;
    
    
    @Test
    public void NX_P52701_should() {

        //ExonMapping m = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_P52701");
    	
    	String entryName = "NX_P52701";
		Optional<GenomicMapping> ogm = genomicMappingService.findGenomicMappingsByEntryName(entryName).stream()
                .filter(genomicMapping -> genomicMapping.isChosenForAlignment())
                .findFirst();

		Assert.assertTrue("We should have some genomic mapping", ogm.isPresent());
		GenomicMapping gm = ogm.get();
		//System.out.println("gm.accession=" + gm.getAccession());   							// ENSG00000116062
		//System.out.println("gm.geneSeqId=" + gm.getGeneSeqId());   							// 3263
		for (IsoformGeneMapping igm: gm.getIsoformGeneMappings()) {
			//System.out.println("iso.accession=" + igm.getIsoformAccession());
			//System.out.println("iso.genename=" +igm.getReferenceGeneName());  				// NX_ENSG00000116062
			for (TranscriptGeneMapping tgm: igm.getTranscriptGeneMappings()) {
				String line = tgm.getIsoformName() + "\t" +  tgm.getDatabase() + "\t" + tgm.getReferenceGeneUniqueName() + "\t" 
						+ tgm.getDatabaseAccession() + "\t" + tgm.getQuality() + "\t" + tgm.getExons().size();
				System.out.println(line);		;
				/*
				System.out.println("tgm.isoformName="+ tgm.getIsoformName());				// NX_P52701-4 
				System.out.println("tgm.database="+ tgm.getDatabase());						// Ensembl
				System.out.println("tgm.refGeneId="+ tgm.getReferenceGeneUniqueName());		// NX_ENSG00000116062
				System.out.println("tgm.databaseAccession="+ tgm.getDatabaseAccession());	// ENST00000234420
				//System.out.println("tgm.proteinId="+ tgm.getProteinId());					// ENSP00000234420
				//System.out.println("tgm.name="+ tgm.getName());								// NX_ENST00000234420
				System.out.println("tgm.quality="+ tgm.getQuality());						// GOLD
				System.out.println("tgm.exons.size="+ tgm.getExons().size());
				*/
			}
		}
		System.out.println("Done");

		
    }
    
    
    
    
}

