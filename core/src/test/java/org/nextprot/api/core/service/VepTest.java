package org.nextprot.api.core.service;

import jaligner.Alignment;
import jaligner.NeedlemanWunsch;
import jaligner.Sequence;
import jaligner.formats.Pair;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@ActiveProfiles({ "dev", "cache" })
public class VepTest extends CoreUnitBaseTest {

    @Autowired
    private GenomicMappingService genomicMappingService;
    @Autowired
    private IsoformService isoformService;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

    /*
     * UI  https://www.ensembl.org/Multi/Tools/VEP?db=core
     * API https://rest.ensembl.org/vep/homo_sapiens/hgvs/ENSP00000386985.1%3Ap.Thr338Ala
     */

    
	@Test
	public void testCompareIsoEnspOf100Entries() {

		List<String> fieldList = Arrays.asList("nxEntryAc,nxIsoAc,nxEnsgAc,nxEnstAc,quality,nxEnspAc,blEnspAc,dataStatus,isoSeqMatch".split(","));
		Map<String,Map<String,Integer>> stats = new HashMap<>();
		StringBuffer hd = new StringBuffer();
		for (String fname : fieldList) {
			hd.append(fname + "\t");
		}
		System.out.println("---------------- data --------------");
		System.out.println(hd);
		for (String entryAc : get100EntryAcs()) {
			for (Isoform iso : isoformService.findIsoformsByEntryName(entryAc)) {
				String isoAc = iso.getIsoformAccession();
				Map<String,String> result = compareEnspAndIso(isoAc);
				StringBuffer sb = new StringBuffer();
				for (String fname : fieldList) {
					String fvalue = result.get(fname);
					sb.append(fvalue + "\t");
					// stats
					if (! stats.containsKey(fname)) stats.put(fname, new HashMap<String,Integer>());
					if (! stats.get(fname).containsKey(fvalue)) stats.get(fname).put(fvalue, 0);
					Integer cnt = new Integer(stats.get(fname).get(fvalue) + 1);
					stats.get(fname).put(fvalue, cnt);
				}
				System.out.println(sb.toString());
			}
		}
		System.out.println("---------------- stats --------------");
		int lng = 30;
		System.out.println(rightPadString("Distinct entries", lng) + "\t" + stats.get("nxEntryAc").size());
		System.out.println(rightPadString("Distinct ENSGs", lng) + "\t" + stats.get("nxEnsgAc").size());
		System.out.println(rightPadString("Distinct isoforms", lng) + "\t" + stats.get("nxIsoAc").size());
		System.out.println(rightPadString("Distinct ENSTs", lng) + "\t" + stats.get("nxEnstAc").size());
		for (String fv : stats.get("quality").keySet()) {
			System.out.println(rightPadString("ENST/Iso map " + fv , lng) + "\t" + stats.get("quality").get(fv));			
		}
		System.out.println(rightPadString("Distinct ENSPs (nx)", lng) + "\t" + stats.get("nxEnspAc").size());
		System.out.println(rightPadString("Distinct ENSPs (ens)", lng) + "\t" + stats.get("blEnspAc").size());
		for (String fv : stats.get("dataStatus").keySet()) {
			System.out.println(rightPadString("Data status " + fv, lng)  + "\t" + stats.get("dataStatus").get(fv));			
		}
		for (String fv : stats.get("isoSeqMatch").keySet()) {
			System.out.println(rightPadString("Iso/ENSP match " + fv, lng) + "\t" + stats.get("isoSeqMatch").get(fv));			
		}
		System.out.println("---------------- end --------------");

	}
    
	private String rightPadString(String s, int lng) {
		StringBuilder sb = new StringBuilder(s);
		
		while (sb.length()<lng) sb.append(" ");
		return sb.toString();
	}
	

	@Test
	public void testRetrieveNpEntryGene() {
	    /*
	     * perl test_ensembl_api.pl ENSG0000014245y > out/NX_Q86X55.ens 2>&1
	     */
		for (String entryAc : get100EntryAcs()) {
			GenomicMapping gm = getGenomicMappingOfEnsgAlignedWithEntry(entryAc);
			if (gm==null) {
				System.out.println("# Found no ENSG for " + entryAc);
			} else { 
				String ensgAc = gm.getAccession();
				// scripts are then executed on cactus:/work/tmp
				System.out.println("perl test_ensembl_api.pl " + ensgAc + " > out/" + entryAc + ".ens 2>&1");
			}
		}		
	}
    
	@Test
	public void testRetrieveNpEntryGeneIsoTranscript() {
		for (String entryAc : get100EntryAcs()) {
			//String ensgAc = getGenomicMappingOfEnsgAlignedWithEntry(entryAc).getAccession();
			for (Isoform iso : isoformService.findIsoformsByEntryName(entryAc)) {
				String isoAc = iso.getIsoformAccession();
				Map<String,String> npMapping = getEnstAlignedWithIsoform(isoAc);
				String ensgAc = npMapping==null ? "---------------" : npMapping.get("ENSG");
				String enstAc = npMapping==null ? "---------------" : npMapping.get("ENST");
				String enspAc = npMapping==null ? "---------------" : npMapping.get("ENSP");
				String quality = npMapping==null ? "----" : npMapping.get("quality");
				System.out.println(entryAc + "\t" + isoAc + "\t" + ensgAc + "\t" + enstAc + "\t" + quality + "\t" + enspAc );
			}
		}		
	}
    
    
	@Test
	public void testRetrieve100RandomEntries() {
		List<String> acs = new ArrayList<>(masterIdentifierService.findUniqueNames());
		for (int i=0;i<100;i++) {
			int index = (int)(Math.random() * acs.size());
			System.out.print(acs.get(index) + ",");
		}
	}
    
    
	@Test
	public void testENSGAlignedWithEntry() {
		Assert.assertEquals("ENSG00000116062", getGenomicMappingOfEnsgAlignedWithEntry("NX_P52701").getAccession());
		Assert.assertEquals("ENSG00000216649", getGenomicMappingOfEnsgAlignedWithEntry("NX_A1L429").getAccession());
	}
	
	
	@Test
	public void testEnsemblApiResultParsing() {
		Map<String,Map<String,String>> enstEnspMap = getEnstEnspMapForEntry("NX_P52701") ;
		for (String t : enstEnspMap.keySet()) {
			for (String p: enstEnspMap.get(t).keySet()) {
				System.out.println(t + "/" + p + " " + enstEnspMap.get(t).get(p) );
			}
		}
	}

	
	@Test
	public void testCompareIsoAndEnspSequences() {
		compareIsoAndEnspSequences("NX_P52701-1");
		compareIsoAndEnspSequences("NX_P52701-3");
		compareIsoAndEnspSequences("NX_P52701-4");
	}

	
	// provides report to be displayed elsewhere
	private Map<String,String> compareEnspAndIso(String nxIsoAc) {
		String nxEntryAc = nxIsoAc.split("-")[0];
		String nxIsoSeq = isoformService.findIsoform(nxIsoAc).getSequence();
		// default record
		Map<String,String> record = new HashMap<>();
		record.put("nxEntryAc", nxEntryAc);
		record.put("nxIsoAc", nxIsoAc);
		record.put("nxIsoSeq", nxIsoSeq);
		record.put("nxEnstAc", "---------------");
		record.put("nxEnsgAc", "---------------");
		record.put("nxEnspAc", "---------------");
		record.put("quality", "----");
		record.put("blEnspAc", "---------------");
		record.put("blEnspSeq", "(no seq)");
		record.put("isoSeqMatch", "Impossible");
		record.put("dataStatus", "nxENSG not found");
		// get nextprot mapping data
		Map<String,String> npMapping = getEnstAlignedWithIsoform(nxIsoAc);
		if (npMapping==null) return record;
		String nxEnsgAc = npMapping.get("ENSG");
		if (nxEnsgAc==null)     { record.put("dataStatus", "nxENSG not found"); return record; }
		record.put("nxEnsgAc", nxEnsgAc);
		String nxEnstAc = npMapping.get("ENST");
		if (nxEnstAc==null)     { record.put("dataStatus", "nxENST not found"); return record; }
		record.put("nxEnstAc", nxEnstAc);
		record.put("quality", npMapping.get("quality"));
		record.put("nxEnspAc", npMapping.get("ENSP"));
		// get ensembl data from its api
		Map<String,Map<String,String>> blMappings = getEnstEnspMapForEntry(nxEntryAc);
		if (blMappings==null) return record;
		Map<String,String> blEnstMapping = blMappings.get(nxEnstAc);
		if (blEnstMapping==null)     { record.put("dataStatus", "ENST not found"); return record; }
		if (blEnstMapping.size()==0) { record.put("dataStatus", "ENST has no ENSP"); return record; }
		String numTr = blEnstMapping.size() > 1 ?  "multiple ENSPs": "1 ENSP";
		record.put("dataStatus", "ENST has " + numTr);
		// now restricting only to the first ENSP
		// all known cases so far have only one ENSP
		String blEnspAc = blEnstMapping.keySet().iterator().next();
		String blEnspSeq = blEnstMapping.get(blEnspAc);
		record.put("blEnspAc", blEnspAc);
		record.put("blEnspSeq", blEnspSeq);
		// compute ENSP / isoform sequence comparison 
		if (nxIsoSeq.equals(blEnspSeq)) {
			record.put("isoSeqMatch", "FULL - nxIsoSeq equals blEnspSeq");				
		} else if (blEnspSeq.contains(nxIsoSeq)) {
			record.put("isoSeqMatch", "FULL - nxIsoSeq  in blEnspSeq");				
		} else if (nxIsoSeq.contains(blEnspSeq)) {
			record.put("isoSeqMatch", "PART - blEnspSeq  in nxIsoSeq");				
		} else {
			record.put("isoSeqMatch", "DIFF - blEnspSeq diff nxIsoSeq");
			align(nxIsoAc, nxIsoSeq, blEnspAc, blEnspSeq);
		}
		return record;
	}
	
	Map<String,String> align(String name1, String seq1, String name2, String seq2) {
		Map<String,String> result = new HashMap<>();
        float match = 2;
        float mismatch = -1;
        float gap = 2;
        Sequence s1 = new Sequence(name1, seq1 );
        Sequence s2 = new Sequence(name2, seq2 );
        Matrix matrix = MatrixGenerator.generate(match, mismatch);
        Alignment alignment = NeedlemanWunsch.align(s1, s2, matrix, gap);
        System.out.println(alignment.getSummary());
        Pair pair = new Pair();
        System.out.println(pair.format(alignment));
		return result;
	}
	
	
	
	// display interesting stuff
	private void compareIsoAndEnspSequences(String isoAc) {
		
		Map<String,String> npMapping = getEnstAlignedWithIsoform(isoAc);
		String npIsoSeq = isoformService.findIsoform(isoAc).getSequence();
		String npEnst = npMapping.get("ENST");
		System.out.println(isoAc + " - " + npEnst);
		String entryAc = isoAc.split("-")[0];
		Map<String,String> enspMap = getEnstEnspMapForEntry(entryAc).get(npEnst);
		if (enspMap==null) {
			System.out.println("ENSP not found");
		} else {
			for (String ensp: enspMap.keySet()) {
				String enspSeq = enspMap.get(ensp);
				System.out.println(ensp);
				System.out.println("iso  : " + npIsoSeq);
				System.out.println("ensp : " + enspSeq);
				if (npIsoSeq.equals(enspSeq)) {
					System.out.println("Iso equals ENSP");					
				} else if (enspSeq.contains(npIsoSeq)) {
					System.out.println("Iso included in ENSP");
				} else if (npIsoSeq.contains(enspSeq)) {
					System.out.println("Iso included in ENSP");
				} else {
					System.out.println("Sequences differ, try alignment...");
				}
			}
		}
		
	}
	
	
	@Test
	public void testGetEnstFromIsoformAC() {
		
		/*
		 
		   Note: transcripts involved in SILVER and BRONZE tr-iso mappings are hidden in the exons page
		   but exon composition is diplayed !!! See NX_A0AVI2, NX_O95996, NX_O95639
		   Question:should we restrict to ENSP related to a transcript involved in GOLD tr-iso mappings ?
		   
		*/
		
		Map result;
		result = getEnstAlignedWithIsoform("NX_P52701-4");
		Assert.assertEquals("ENSG00000116062", result.get("ENSG"));
		Assert.assertEquals("ENST00000540021", result.get("ENST"));
		Assert.assertEquals("ENSP00000446475", result.get("ENSP"));
		Assert.assertEquals("GOLD", result.get("quality"));

		result = getEnstAlignedWithIsoform("NX_Q15813-1");
		Assert.assertEquals("ENSG00000284770", result.get("ENSG"));
		Assert.assertEquals("ENST00000406207", result.get("ENST"));
		Assert.assertEquals("ENSP00000384571", result.get("ENSP"));
		Assert.assertEquals("GOLD", result.get("quality"));
	
	}
	
	private Map<String,String> getEnstAlignedWithIsoform(String isoAc) {
		Map<String,String> result = new HashMap<>();
		result.put("ENSG", "---------------");
		result.put("ENST", "---------------");
		result.put("ENSP", "---------------");
		result.put("quality", "----");
		String entryAc = isoAc.split("-")[0];
		GenomicMapping gm = getGenomicMappingOfEnsgAlignedWithEntry(entryAc);
		if (gm==null) return result;
		result.put("ENSG", gm.getAccession());
		for (IsoformGeneMapping igm : gm.getIsoformGeneMappings()) {
			if (igm.getIsoformAccession().equals(isoAc)) {
				List<TranscriptGeneMapping> tgmList = igm.getTranscriptGeneMappings();
				if (tgmList==null || tgmList.size()==0) return result;
				// the first mapping in list is the shortest and is always chosen as "main" (or best) transcript
				TranscriptGeneMapping tgm = tgmList.get(0); 
				result.put("ENST", tgm.getDatabaseAccession());
				if (tgm.getProteinId()!=null) result.put("ENSP", tgm.getProteinId());
				result.put("quality", tgm.getQuality());
				return result;
			}
	  }
	  return null;
	}
	
	private GenomicMapping getGenomicMappingOfEnsgAlignedWithEntry(String entryAc) {
		List<GenomicMapping> gmaps = genomicMappingService.findGenomicMappingsByEntryName(entryAc);
		if (gmaps==null) return null;
		for (GenomicMapping gm : gmaps) {
			if (gm.isChosenForAlignment()) return gm;
		}
		return null;
	}
	
	private Map<String,Map<String,String>> getEnstEnspMapForEntry(String entryAc) {
		Map<String,Map<String,String>> result = new HashMap<>();
		
		String currEnst = "";
		String currEnsp = "";
		for (String line: getEnsemblApiResultForEntry(entryAc)) {
			if (line.startsWith("ENSG")) {
				// ignore it we dont need it
			} else if (line.startsWith("ENST")) {
				currEnst = line.split(" ")[0];
				result.put(currEnst, new HashMap<String,String>());
			} else if (line.startsWith("ENSP")) {
				currEnsp = line;
			} else {
				String seq = line;
				result.get(currEnst).put(currEnsp, seq);
			}
		}
		return result;
		
	}
	
	
	private List<String> get100EntryAcs() {	
		String acs = "NX_P52701,NX_Q6ZSA8,NX_Q5T8R8,NX_Q9H0Z9,NX_Q00341,NX_Q9NTU4,NX_A6NMZ7,NX_Q9H4Q4,NX_Q6IS24,NX_Q9BWU1,NX_P15144,NX_Q96R45,NX_Q86X55,NX_O00755,NX_Q96G74,NX_P15311,NX_Q96RD7,NX_P01579,NX_P01178,NX_Q9P2B7,NX_Q9HAA7,NX_Q6RI45,NX_Q9NW15,NX_A0A1B0GV85,NX_O94778,NX_B2RPK0,NX_Q9UI12,NX_P42330,NX_Q32Q52,NX_Q8WWW8,NX_P32927,NX_Q16281,NX_Q9Y2Y1,NX_O75771,NX_P09238,NX_P60709,NX_P60981,NX_Q9Y247,NX_Q7Z4T8,NX_Q9UQG0,NX_P25929,NX_Q9Y2A7,NX_Q6H9L7,NX_P60842,NX_Q9HCC9,NX_P60174,NX_Q4AE62,NX_Q9NRW7,NX_Q9NQA5,NX_Q9NZR4,NX_P08133,NX_Q5T7P8,NX_Q06730,NX_Q9GZU0,NX_Q6FHJ7,NX_Q9UJ04,NX_O95870,NX_P46089,NX_O15194,NX_P09497,NX_Q92484,NX_Q504Q3,NX_P98173,NX_Q9UQG0,NX_Q8N474,NX_A6NEY3,NX_Q9UKG9,NX_Q9C0D7,NX_O75309,NX_P29536,NX_Q9NY99,NX_Q96M29,NX_P04628,NX_Q9HB07,NX_P12830,NX_Q8WV41,NX_P04271,NX_P36952,NX_P0C8F1,NX_A0PJX0,NX_P19634,NX_P05067,NX_Q9NX00,NX_Q9P2M4,NX_Q99466,NX_Q13501,NX_P29016,NX_Q14766,NX_P14735,NX_P51649,NX_Q401N2,NX_Q96H55,NX_Q9Y5F9,NX_H3BQB6,NX_P28325,NX_Q9Y3A0,NX_Q9H5J4,NX_P20908,NX_P07197,NX_P49711,NX_Q2TAK8";
		return Arrays.asList(acs.split(","));
	}
	
	private List<String> getEnsemblApiResultForEntry(String ac)  {
		try {
			return Files.readAllLines(Paths.get("/Users/pmichel/tmp/vep/out/" + ac + ".ens"));
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
	
	/*	
	
	private Map<String,Map<String,String>> getMsh6EnstEnspMap() {
		
		Map<String,Map<String,String>> result = new HashMap<>();
		
		String currEnst = "";
		String currEnsp = "";
		for (String line: getEnsemblApiResultForMsh6().split("\n")) {
			if (line.startsWith("ENST")) {
				currEnst = line.split(" ")[0];
				result.put(currEnst, new HashMap<String,String>());
			} else if (line.startsWith("ENSP")) {
				currEnsp = line;
			} else {
				String seq = line;
				result.get(currEnst).put(currEnsp, seq);
			}
		}
		return result;
	}

	private String getEnsemblApiResultForMsh6() {
		
		// NX_P52701 - MSH6 - ENSG00000116062
		
		return  "ENST00000607272 is non-coding\n" + 
				"ENST00000454137 is non-coding\n" + 
				"ENST00000652107\n" + 
				"ENSP00000498629\n" + 
				"MEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRVISDSESDIGGSDVEFKPDTKEEGSSDEISSGVGDSESEGLNSPVKVARKRKRMVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000614496\n" + 
				"ENSP00000477844\n" + 
				"MVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000622629\n" + 
				"ENSP00000482078\n" + 
				"MRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAIVCLLLDQIWGASLRLYSQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHISRRLFSKCCCALGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000445503\n" + 
				"ENSP00000405294\n" + 
				"MSRQSTLYSFFPKSPALSDANKASARASREGGRAAAAPGASPSPGGDAAWSEAGPGPRPLARSASPPKAKNLNGGLRRSVAPAAPTSCDFSPGDLVWAKMEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGRHNLRNR\n" + 
				"ENST00000456246\n" + 
				"ENSP00000410570\n" + 
				"MSRQSTLYSFFPKSPALSDANKASARASREGGRAAAAPGASPSPGGDAAWSEAGPGPRPLARSASPPKAKNLNGGLRRSVAPAAPTRFKIKGSPEGRSFLQCKA\n" + 
				"ENST00000673922 is non-coding\n" + 
				"ENST00000234420\n" + 
				"ENSP00000234420\n" + 
				"MSRQSTLYSFFPKSPALSDANKASARASREGGRAAAAPGASPSPGGDAAWSEAGPGPRPLARSASPPKAKNLNGGLRRSVAPAAPTSCDFSPGDLVWAKMEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRVISDSESDIGGSDVEFKPDTKEEGSSDEISSGVGDSESEGLNSPVKVARKRKRMVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000616033\n" + 
				"ENSP00000480261\n" + 
				"MSRQSTLYSFFPKSPALSDANKASARASREGGRARCPRGLSFPRRGCGLERGWAWARPLARSASPPKAKNLNGGLRRSVAPAAPTSCDFSPGDLVWAKMEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRVISDSESDIGGSDVEFKPDTKEEGSSDEISSGVGDSESEGLNSPVKVARKRKRMVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLGKTLNKLVLRL\n" + 
				"ENST00000673637\n" + 
				"ENSP00000501310\n" + 
				"MEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRVISDSESDIGGSDVEFKPDTKEEGSSDEISSGVGDSESEGLNSPVKVARKRKRMVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000493177 is non-coding\n" + 
				"ENST00000540021\n" + 
				"ENSP00000446475\n" + 
				"MSRQSTLYSFFPKSPALSDANKASARASREGGRAAAAPGASPSPGGDAAWSEAGPGPRPLARSASPPKAKNLNGGLRRSVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRVISDSESDIGGSDVEFKPDTKEEGSSDEISSGVGDSESEGLNSPVKVARKRKRMVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000455383\n" + 
				"ENSP00000397484\n" + 
				"MEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTY\n" + 
				"ENST00000420813\n" + 
				"ENSP00000390382\n" + 
				"MEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPS\n" + 
				"ENST00000538136\n" + 
				"ENSP00000438580\n" + 
				"MVTGNGSLKRKSSRKETPSATKQATSISSETKNTLRAFSAPQNSESQAHVSGGGDDSSRPTVWYHETLEWLKEEKRRDEHRRRPDHPDFDASTLYVPEDFLNSCTPGMRKWWQIKSQNFDLVICYKVGKFYELYHMDALIGVSELGLVFMKGNWAHSGFPEIAFGRYSDSLVQKGYKVARVEQTETPEMMEARCRKMAHISKYDRVVRREICRIITKGTQTYSVLEGDPSENYSKYLLSLKEKEEDSSGHTRAYGVCFVDTSLGKFFIGQFSDDRHCSRFRTLVAHYPPVQVLFEKGNLSKETKTILKSSLSCSLQEGLIPGSQFWDASKTLRTLLEEEYFREKLSDGIGVMLPQVLKGMTSESDSIGLTPGEKSELALSALGGCVFYLKKCLIDQELLSMANFEEYIPLDSDTVSTTRSGAIFTKAYQRMVLDAVTLNNLEIFLNGTNGSTEGTLLERVDTCHTPFGKRLLKQWLCAPLCNHYAINDRLDAIEDLMVVPDKISEVVELLKKLPDLERLLSKIHNVGSPLKSQNHPDSRAIMYEETTYSKKKIIDFLSALEGFKVMCKIIGIMEEVADGFKSKILKQVISLQTKNPEGRFPDLTVELNRWDTAFDHEKARKTGLITPKAGFDSDYDQALADIRENEQSLLEYLEKQRNRIGCRTIVYWGIGRNRYQLEIPENFTTRNLPEEYELKSTKKGCKRYWTKTIEKKLANLINAEERRDVSLKDCMRRLFYNFDKNYKDWQSAVECIAVLDVLLCLANYSRGGDGPMCRPVILLPEDTPPFLELKGSRHPCITKTFFGDDFIPNDILIGCEEEEQENGKAYCVLVTGPNMGGKSTLMRQAGLLAVMAQMGCYVPAEVCRLTPIDRVFTRLGASDRIMSGESTFFVELSETASILMHATAHSLVLVDELGRGTATFDGTAIANAVVKELAETIKCRTLFSTHYHSLVEDYSQNVAVRLGHMACMVENECEDPSQETITFLYKFIKGACPKSYGFNAARLANLPEEVIQKGHRKAREFEKMNQSLRLFREVCLASERSTVDAEAVHKLLTLIKEL\n" + 
				"ENST00000411819\n" + 
				"ENSP00000406248\n" + 
				"MEGYPWWPCLVYNHPFDGTFIREKGKSVRVHVQFFDDSPTRGWVSKRLLKPYTGSKSKEAQKGGHFYSAKPEILRAMQRADEALNKDKIKRLELAVCDEPSEPEEEEEMEVGTTYVTDKSEEDNEIESEEEVQPKTQGSRRSSRQIKKRRV";
	}
*/


}


/*
 * Perl code to get ENSP sequences from an ENSG using ensembl API
 * 

# Works on cactus given this env variable:
# export PERL5LIB=/mnt/npdata/ensembl_mirror/npteam/api/git/src/ensembl/modules/

 
use Bio::EnsEMBL::Registry

my $host = 'bernard.isb-sib.ch';
my $port = 3306;
my $user = 'ensembl';
my $pass = 'Juve.2013';



my $registry = 'Bio::EnsEMBL::Registry';

$registry->load_registry_from_db(
         -host => 'bernard.isb-sib.ch',
         -port => $port,
         -user => $user,
         -pass => $pass
      );
#my $stable_id = 'ENST00000540021';
#my $transcript = $adaptor->fetch_by_stable_id($stable_id);
#print $transcript->stable_id(), "\n";
#print $transcript->spliced_seq(),         "\n";
#print length($transcript->spliced_seq()), "\n";
#print $transcript->translateable_seq(),         "\n";
#print length($transcript->translateable_seq()), "\n";
#print $transcript->translation()->stable_id(), "\n";
#print $transcript->translate()->seq(),         "\n";
#print length($transcript->translate()->seq()), "\n";

my $adaptor = $registry->get_adaptor( "human", "core", "Gene");
my $gene = $adaptor->fetch_by_stable_id('ENSG00000116062');
foreach my $transcript ( @{ $gene->get_all_Transcripts() } ) {
  if ( $transcript->translation() ) {
    print  $transcript->stable_id(),  "\n";
    print $transcript->translation()->stable_id(), "\n";
    print $transcript->translate()->seq(),         "\n";
    foreach my $translation  ( @{ $transcript->get_all_alternative_translations() } ) {
      print $translation->stable_id(), "\n";
      print $translation->seq(), "\n";
    }
  } else {
    print $transcript->stable_id(), " is non-coding\n";
  }
}


 */
