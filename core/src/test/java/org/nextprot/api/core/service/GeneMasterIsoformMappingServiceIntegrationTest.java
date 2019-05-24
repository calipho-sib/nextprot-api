package org.nextprot.api.core.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev" })
public class GeneMasterIsoformMappingServiceIntegrationTest extends CoreUnitBaseTest {
        
    @Autowired
	private GeneMasterIsoformMappingService geneMasterIsoformMappingService;
    @Autowired
	private IsoformService isoService;
    @Autowired
	private GenomicMappingService gmapService;

    @Test
    public void NX_ENSG00000178199_hasSomeExons() {
    	String geneName = "NX_ENSG00000178199";
    	List<SimpleExonWithSequence> exonList = geneMasterIsoformMappingService.findGeneExons(geneName);
    	Assert.assertTrue(exonList.size()>0);
    	SimpleExonWithSequence ex = exonList.get(0);
    	Assert.assertEquals("ENSE00001860500", ex.getAccession());
    	Assert.assertEquals("NX_ENSG00000178199", ex.getGeneRegion().getGeneName());
    	Assert.assertEquals(1, ex.getGeneRegion().getFirstPosition());
    	Assert.assertEquals(249, ex.getGeneRegion().getLastPosition());
    	Assert.assertEquals("NX_ENSE00001860500", ex.getName());
    	Assert.assertEquals("CTTAAAAAAAAAAAAACCAAAAAAACCCAAAGCATAACTACTTTTGCAGCTGAACGTGACTGTGGCGTGCAGGAAGTGGAGCATTGGCATGAAGTGGCTCCTAGTGGCTGCTTGGCGCACGCCAGCTGCCCTCCTCTGACTCCAGTGGCACCTGGGGGCCTGGCCTCACTGACGGGAGAACATTGGCGTGAAGGCTGCTGGCGACTGGGCCAGCATTCATTGTGAAGACCGGAGGGACACACCCTGCTG", ex.getSequence());
    }
    
    @Test
    public void NX_P47710_hasSomeGeneRegionsOf_NX_ENSG00000126545() {
    	String entryName = "NX_P47710";
    	List<GeneRegion> regions = geneMasterIsoformMappingService.findEntryGeneRegions(entryName);
    	Assert.assertTrue(regions.size()>0);
    	int prevPos=-1;
    	for (GeneRegion r : regions) {
    		Assert.assertEquals("NX_ENSG00000126545", r.getGeneName());
    		Assert.assertTrue(r.getFirstPosition()>prevPos);
    		prevPos=r.getLastPosition(); // regions should not overlap (except some...)
    	}
    	
    }
    
    @Test
    public void testMSH6_step1() {
    	String entryName = "NX_P52701";
    	// get gene-master mapping positions
    	List<GeneRegion> regions = geneMasterIsoformMappingService.findEntryGeneRegions(entryName);
    	Assert.assertEquals(18, regions.size());
    	int prevPos=-1;
    	for (GeneRegion r : regions) {
    		Assert.assertTrue(r.getFirstPosition()>prevPos);
    		prevPos=r.getLastPosition(); // regions should not overlap (except some...)
    	}
    	// get gene exons
    	String geneName = regions.get(0).getGeneName();
    	List<SimpleExonWithSequence> exonList = geneMasterIsoformMappingService.findGeneExons(geneName);
    	System.out.println(exonList.size());
    	int rgEqualsEx = 0;
    	int rgIncludedInEx = 0;
    	int rgIncludesEx = 0;
    	
    	for (GeneRegion r: regions) {
			int rgp1=r.getFirstPosition();
			int rgp2=r.getLastPosition();
    		for (SimpleExonWithSequence ex: exonList) {
    			int exp1=ex.getFirstPositionOnGene();
    			int exp2=ex.getLastPositionOnGene();
    			String exac = ex.getAccession();
    			int exrk = ex.getRank();
        	    String status="";
        	    status += "rg " + rgp1 + "-" + rgp2;  
        	    status += "  ex" + exrk + " " + exac + " " + exp1 + "-" + exp2;  
        	    
    			if (rgp1==exp1 && rgp2==exp2) {
    				rgEqualsEx++;
    				System.out.println(status + " REGION = EXON");
    			} else if (rgp1 >= exp1 && rgp2 <= exp2) {
    				rgIncludedInEx++;
    				System.out.println(status + " REGION included in EXON");    				
				} else if (rgp1 <= exp1 && rgp2 >= exp2) {
					rgIncludesEx++;
					System.out.println(status + " REGION includes EXON");    				
				}
    		}
    	}
    	System.out.println("Region                   count:" + regions.size());
    	System.out.println("Region equals      Exon, count:" + rgEqualsEx);
    	System.out.println("Region included in Exon, count:" + rgIncludedInEx);
    	System.out.println("Region includes    Exon, count:" + rgIncludesEx);
    }

    @Test
    public void testMSH6_step2() {
    	String entryName = "NX_P52701";
    	// get gene-master mapping positions
    	List<GeneRegion> regions = geneMasterIsoformMappingService.findEntryGeneRegions(entryName);
    	// get gene exons
    	String geneName = regions.get(0).getGeneName();
    	List<SimpleExonWithSequence> exonList = geneMasterIsoformMappingService.findGeneExons(geneName);
    	System.out.println("Region count:" + regions.size());
    	String mastersequence = buildMasterSequenceFromMasterGeneMappingAndExons(regions, exonList);
    	System.out.println("Master sequence lng:" + mastersequence.length());
    	System.out.println(mastersequence);

    	List<Isoform> isoforms = isoService.findIsoformsByEntryName(entryName);
    	for (Isoform iso: isoforms) {
    		System.out.println(iso.getIsoformAccession());
    		StringBuffer isoBuf = new StringBuffer();
    		for (NucleotidePositionRange npr : iso.getMasterMapping()) {
    			int npr1=npr.getLower();
    			int npr2=npr.getUpper();
    			System.out.println("" + npr1 + "-" + npr2);
    			isoBuf.append(mastersequence.substring(npr1-1,  npr2));
    		}
    		System.out.println("iso nu lng  : " + isoBuf.length());  
    		System.out.println("iso nu list : " + isoBuf.toString());
    		StringBuffer isorf0 = new StringBuffer();
        	for (int i=0;i<isoBuf.length();i+=3) {
        		String codon0 = isoBuf.substring(i,i+3);
        		isorf0.append(AminoAcidCode.valueOfAminoAcidFromCodon(codon0).get1LetterCode());
        	}
    		System.out.println(   "iso aa list : " + isorf0.toString());
        	System.out.println( "iso   seq   : " +iso.getSequence());
    	}

    }

    @Test
    public void testpam() {
    	String entryName = "NX_P52701";

    	// why List ? cos we can have mapping not used for alignments !
    	List<GenomicMapping> gmlist = gmapService.findGenomicMappingsByEntryName(entryName);
    	System.out.println("genomic mapping size: " + gmlist.size());
    	GenomicMapping gm = gmlist.get(0);
    	System.out.println("ac:" + gm.getAccession() + 
    			" - for alignment:" +gm.isChosenForAlignment() + 
    			" - low quality tra-gen mappings:" + gm.isLowQualityMappings());
    	
    	for (String s: gm.getNonMappingIsoforms()) System.out.println(s + " is UNMAPPED");
    	
    	List<SimpleExonWithSequence> exonList = geneMasterIsoformMappingService.findGeneExons("NX_"+ gm.getAccession());
    	List<IsoformGeneMapping> igmlist = gm.getIsoformGeneMappings();
    	for (IsoformGeneMapping igm: igmlist) {
    		System.out.println(igm.getIsoformAccession() + " - " + igm.getIsoformMainName());
    		for (GeneRegion region : igm.getIsoformGeneRegionMappings()) {
    			int p1 = region.getFirstPosition();
    			int p2 = region.getLastPosition();
    			int lng = region.getLength();
    			System.out.println("iso-gene region: " + p1 + " - " + p2 + " lng:" + lng);
    			String dna = buildDNASequenceForGeneRegion(region,exonList);
    			StringBuffer aaseq = new StringBuffer();
    			System.out.println(dna);
    			for (int i=0;i<dna.length()-10;i=i+3) {
    				String codon = dna.substring(i, i+3);
    				aaseq.append(AminoAcidCode.valueOfAminoAcidFromCodon(codon).get1LetterCode());
    			}
    			System.out.println(aaseq.toString());
    		}
    	}
    	

    	
    }

    static void buildIsoDNASequence(List<GeneRegion> geneRegions, List<SimpleExonWithSequence> exonList) {
    	for (GeneRegion r: geneRegions) {
    		String seq = buildDNASequenceForGeneRegion(r, exonList);
    	}
    }
    static String buildDNASequenceForGeneRegion(GeneRegion region, List<SimpleExonWithSequence> exonList) {
    	StringBuffer sb = new StringBuffer();
    	int rgp1=region.getFirstPosition();
    	int rgp2=region.getLastPosition();
    	int rlng = region.getLength();
    	for (SimpleExonWithSequence ex: exonList) {
    		int exp1 = ex.getFirstPositionOnGene();
    		int exp2 = ex.getLastPositionOnGene();
    		//System.out.println("rg:" + rgp1 + "-" + rgp2 + " - ex:" + exp1 + "-" + "exp2:" + exp2);
    		
    		// CASE 1 - region included in exon
    		if (rgp1 >= exp1 && rgp2 <=exp2) {
    			int i1 = rgp1-exp1;
    			int i2 = i1 + (rgp2-rgp1+1);
    			String exac = ex.getAccession();
    			String dna = ex.getSequence().substring(i1, i2);
    			System.out.println("i1:" + i1 + " - i2:" + i2 + " - rlng:" + rlng + " - dnalng:" + dna.length() + " - exon:" + exac);
    			return dna;
    			
    		// region starts in exon but upper bound is beyond exon upper bound
    		} else if (rgp1 >= exp1 && rgp2 > exp2) {
    		}
		
        	// region starts in exon but upper bound is beyond exon upper bound
    		} else if (rgp1 >= exp1 && rgp2 > exp2) {
    		}
    	}
    	return sb.toString();
    }
    
    
    /**
     * Simple naive master sequence building algorithm
     * Only accepts regions from master-gene mapping that exactly match an exon 
     * If no such exon is found for some region the method throws an error
     * @param regions a list of gene position ranges ordered by first position on gene
     * @param exonList a list of exons ordered by first position on gene
     * @return a master DNA sequence
     */
    static String buildMasterSequenceFromMasterGeneMappingAndExons(List<GeneRegion> regions, List<SimpleExonWithSequence> exonList) {

    	StringBuffer sb = new StringBuffer();
    	int matchCnt=0;
    	int rangeLength=0;
    	for (GeneRegion r: regions) {
    		rangeLength += r.getLength();
			String exonSequence = getExonSequenceFromRegion(r, exonList);
			sb.append(exonSequence);
			matchCnt++;
    	}
    	System.out.println("MatchCnt:" + matchCnt);
    	System.out.println("RangeLng:" + rangeLength);
    	
    	return sb.toString();
    }
    

    /**
     * Get the exon sequence of the exon in the list that exactly match the range defined in the gene region
     * @param region a GeneRegion, first/lastPositionOnGene define a range of positions
     * @param exonList
     * @return the nucleotid sequence of the exon or an error if no exon was found
     */
    static String getExonSequenceFromRegion(GeneRegion region, List<SimpleExonWithSequence> exonList) {
		int rgp1=region.getFirstPosition();
		int rgp2=region.getLastPosition();
		for (SimpleExonWithSequence ex: exonList) {
			int exp1=ex.getFirstPositionOnGene();
			int exp2=ex.getLastPositionOnGene();
			if (rgp1==exp1 && rgp2==exp2) {
				System.out.println("REGION = EXON:" + " [" + rgp1 + "-" + rgp2 + "] " + ex.getAccession() );
				System.out.println("len:" + ex.getSequence().length() + " " + ex.getSequence());
				return ex.getSequence();
			}
		}
		String msg = "ERROR: found no exon matching exactly tgene master mapping region:";
		msg += " [" + rgp1 + "-" + rgp2 + "]";
		throw new RuntimeException(msg);
    }
    
    
    
}

