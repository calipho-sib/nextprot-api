package org.nextprot.api.core.utils.seqmap;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ActiveProfiles({ "dev","cache" })
public class IsoformSequencePositionMapperIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	
	//@Test
	public void testPropagationForVariantsOfAllEntries() throws Exception {
		openLogger("testPropagationForVariantsOfAllEntries.log");
		Set<String> acs = masterIdentifierService.findUniqueNames();
		int cnt=0;
		int entriesWithErrors=0;
		boolean working=false;
		for (String ac: acs) {
			cnt++;
			if (working) {
				//if (sout) System.out.println("--- START testing propagation for variants of entry no. " + cnt + ":" + ac);
				int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry(ac);
				if (errorCnt>0) entriesWithErrors++;
				log(new Date() + " - " + cnt + " - " + ac + (errorCnt==0 ? " OK" : " has " +errorCnt + " ERROR(s)"));
				//if (sout) System.out.println("--- END   testing propagation for variants of entry no. " + cnt + ":" + ac + (errorCnt==0 ? ": OK":": with " +errorCnt + " ERRORs"));;
			}
			if (ac.equals("NX_Q86YS6")) working=true; // start just after last entry tested
		}
		log.close();
		Assert.assertEquals(0, entriesWithErrors);
	}

	
/*
 * 
 * last entry tested so far: NX_Q86YS6
 * Known errors so far:
 * 
NX_O00555 has 1 ERROR(s)
NX_P46937 has 1 ERROR(s)
NX_P34810 has 1 ERROR(s)
NX_P33527 has 1 ERROR(s)
NX_Q9HB55 has 1 ERROR(s)
NX_P19544 has 1 ERROR(s)
NX_Q96NU1 has 1 ERROR(s)
NX_Q8N9B5 has 1 ERROR(s)	
NX_Q86UR1 has 1 ERROR(s)
NX_Q9NUA8 has 1 ERROR(s)
NX_Q9P275 has 1 ERROR(s)
NX_Q02078 has 1 ERROR(s)
NX_P78324 has 1 ERROR(s)
NX_Q9NPQ8 has 1 ERROR(s)
NX_Q96K49 has 1 ERROR(s)
NX_Q00653 has 1 ERROR(s)
NX_Q9UPQ7 has 1 ERROR(s)
NX_O95825 has 1 ERROR(s)
NX_Q86VQ3 has 1 ERROR(s)
NX_Q96QH2 has 1 ERROR(s)
NX_Q9UJW3 has 1 ERROR(s)
 */
	


	//@Test
	public void testPropagationForVariantsOfNX_P78324() throws Exception {
		// 1. first know error
		// TODO see with Anne, fix it or ignore it !
		// known error: interpretation
		// variant 129 PD->D should not project on iso-4 because the P at 129 is at the end of an exon 
		// the deletion is at the border of an exon ? and would impact on splicing ? 
				int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry("NX_P78324");
		Assert.assertEquals(0, errorCnt);
	}

	@Test
	public void testPropagationForVariantsOfNX_P20591() throws Exception {
		int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry("NX_P20591");
		Assert.assertEquals(0, errorCnt);
	}

	@Test
	public void testPropagationForVariantsOfNX_O15503() throws Exception {
		int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry("NX_O15503");
		Assert.assertEquals(0, errorCnt);
	}

	@Test
	public void testPropagationForVariantsOfNX_P05019() throws Exception {
		int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry("NX_P05019");
		Assert.assertEquals(0, errorCnt);
	}

	@Test
	public void testSingleVariantWithInvalidNucleotideIndice() throws Exception {
				
		String entry_ac = "NX_O00115";
		String iso_ac = "NX_O00115-1";
		String variant_ac = "AN_O00115_000472";
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entry_ac).withTargetIsoforms().withAnnotations());
		for (Annotation a: entry.getAnnotations()) {
			if (a.getUniqueName().equals(variant_ac)) {
				int pos = a.getTargetingIsoformsMap().get(iso_ac).getFirstPosition();
				Isoform iso = IsoformUtils.getIsoformByName(entry, iso_ac);
				GeneMasterCodonPosition nuPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(pos, iso);
				for (Isoform iso2: entry.getIsoforms()) {
					if (!iso2.equals(iso)) {
						CodonNucleotideIndices nuIdx = IsoformSequencePositionMapper.getCodonNucleotideIndices(nuPos, iso2);
						Assert.assertEquals(false, nuIdx.has3Nucleotides()); // cannot be projected to iso2
						Assert.assertEquals(false,nuIdx.areConsecutive());
						Assert.assertEquals(false,nuIdx.areInFrame());
						Assert.assertNull(nuIdx.getAminoAcidPosition());
					}
				}
				return;
			}
		}
		Assert.assertTrue(false);

	}	
	@Test
	public void testSingleVariantPositionOnMaster() throws Exception {
		
		// just to be aware of difference between db info and api info
		
		String entry_ac = "NX_P01308";
		String iso_ac = "NX_P01308-1";
		String variant_ac = "AN_P01308_001747";
		int expectedBeginPosOnMaster = 502;
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entry_ac).withTargetIsoforms().withAnnotations());
		for (Annotation a: entry.getAnnotations()) {
			if (a.getUniqueName().equals(variant_ac)) {
				int pos = a.getTargetingIsoformsMap().get(iso_ac).getFirstPosition();
				Isoform iso = IsoformUtils.getIsoformByName(entry, iso_ac);
				GeneMasterCodonPosition nuPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(pos, iso);
//				System.out.println("isoform position                                               : " + pos);
//				System.out.println("nuPos is valid                                                 : " + nuPos.isValid());
//				System.out.println("master position according to iso mapper service                : " + nuPos.getNucleotidePosition(0));
//				System.out.println("master position according to table identifier_feature_position : " + expectedBeginPosOnMaster);
//				System.out.println("master first_position for Anne                                 : " + (nuPos.getNucleotidePosition(0) - 1));
				// we then have to gie the last_positon to Anne, there are 2 cases:
				// case 1: original AAs = single AA
				// => master last_positon fo Anne = first_position for Anne + 3
				// case 2: original AAs length has more than one AAs
				// compute position on master of last AA (same process as above), return nuPos(0)-1 found for iso pos)
				
				// we expect a difference of 1 between what we have in db and what we have from api
				Assert.assertEquals(new Integer(expectedBeginPosOnMaster + 1), new Integer(nuPos.getNucleotidePosition(0)));
				return;
			}
		}
		Assert.assertTrue(false);
		
		/*
		 * SQL to get master position for this variant
		 * 
		select a.unique_name, a.cv_annotation_type_id, pfp.first_pos,pfp.last_pos,ifp.first_pos as master_frist_pos, ifp.last_pos as master_last_pos from sequence_identifiers si
		inner join annotations a on (a.identifier_id=si.identifier_id)
		inner join annotation_protein_assoc apa on (a.annotation_id=apa.annotation_id)
		inner join protein_feature_positions pfp on (apa.assoc_id=pfp.annotation_protein_id)
		inner join identifier_feature_positions ifp on (ifp.annotation_id=a.annotation_id)
		where si.unique_name='NX_P01308' 
		and pfp.first_pos=20;
		
		SQL result:
		unique_name	        cv_annotation_type_id	first_pos	last_pos	master_frist_pos	master_last_pos
        AN_P01308_001839    1027                    20          21          430                 433
		(1 row)
		
		 */
	}


	
	public int getErrorsDuringPropagationOnVariantsOfSingleEntry(String entry_ac) throws Exception {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entry_ac).withTargetIsoforms().withAnnotations());

		int delCount = 0;
		int subCount = 0;
		int insCount = 0;
		int otherCount = 0;
		int errorCount = 0;
		
		for (Annotation a : entry.getAnnotations()) {
			if (a.getAPICategory().equals(AnnotationCategory.VARIANT)) {
				// for each variant annotation
				String ori = a.getVariant().getOriginal();
				String mut = a.getVariant().getVariant();

				if (ori.length() == 1 && mut.length() == 1) {        subCount++;
				} else if (ori.length() == 1 && mut.length() == 0) { delCount++;
				} else if (ori.length() == 0 && mut.length() == 1) { insCount++;
				}

				Map<String, Integer> isoExpectedPos = getExpectedPosForEachIsoform(entry, a);
				printExpectedPosForEachIsoform(isoExpectedPos, a);

				boolean errorOnVariant = false;

				for (String iso1name : isoExpectedPos.keySet()) {
					Integer iso1ExpectedPos = isoExpectedPos.get(iso1name);
					Isoform iso1 = IsoformUtils.getIsoformByName(entry, iso1name);
					if (iso1ExpectedPos != null) {

						GeneMasterCodonPosition nuPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(iso1ExpectedPos, iso1);

						if (!nuPos.isValid()) {
							errorOnVariant = true;
							//System.out.println("ERROR1: codon positions not found for " + iso1name + " for variant at position: " + iso1ExpectedPos);
							continue;
						}

						printIsoLengthAndRangesNuCount(iso1.getUniqueName(), iso1.getSequence(), iso1.getMasterMapping());
//						System.out.println("Starting variant propagation from isoform " + iso1name + " at position " + iso1ExpectedPos);
//						System.out.println(getSequenceWithHighlighedPos(iso1.getSequence(), iso1ExpectedPos));

						for (Isoform iso2 : entry.getIsoforms()) {
							String iso2name = iso2.getUniqueName();
							if (iso2name.equals(iso1name))	continue;
	
							CodonNucleotideIndices nuIdx = IsoformSequencePositionMapper.getCodonNucleotideIndices(nuPos, iso2);
							Integer iso2ActualPos = nuIdx.getAminoAcidPosition();
							Integer iso2ExpectedPos = isoExpectedPos.get(iso2name);
							//System.out.println("Variant " + a.getUniqueName() + " position on isoform " + iso2name + " is "	+ iso2ActualPos);
							printIsoLengthAndRangesNuCount(iso2.getUniqueName(),iso2.getSequence(), iso2.getMasterMapping());

							if (iso2ActualPos == null && iso2ExpectedPos == null) {
								// OK
							} else if (iso2ActualPos == null || iso2ExpectedPos == null) {
								errorOnVariant = true;
								//System.out.println("ERROR2: variant position on isoform " + iso2name + " is " + iso2ActualPos + ", expected " + iso2ExpectedPos);
							} else if (!iso2ActualPos.equals(iso2ExpectedPos)) {
								errorOnVariant = true;
								//System.out.println("ERROR3: variant position on isoform " + iso2name + " is " + iso2ActualPos + ", expected " + iso2ExpectedPos);
							}
						}
					}
				}
				if (errorOnVariant)	errorCount++;
				if (errorOnVariant)	break;
			}
		}

//		System.out.println("Summary " + entry.getUniqueName());
//		System.out.println("insCount:" + insCount);
//		System.out.println("delCount:" + delCount);
//		System.out.println("subCount:" + subCount);
//		System.out.println("otherCount:" + otherCount);
//		System.out.println("errorCount:" + errorCount);
		return errorCount;
	}

	@Test
	public void test1() {

//		System.out.println("pos 3:" + getSequenceWithHighlighedPos("12345", 3));
//		System.out.println("pos 1:" + getSequenceWithHighlighedPos("12345", 1));
//		System.out.println("pos 5:" + getSequenceWithHighlighedPos("12345", 5));
	}

	private Map<String, Integer> getExpectedPosForEachIsoform(Entry entry, Annotation a) {
		Map<String, Integer> isoExpectedPos = new HashMap<String, Integer>();
		for (Isoform isoform : entry.getIsoforms()) {
			String isoname = isoform.getUniqueName();
			AnnotationIsoformSpecificity spec = a.getTargetingIsoformsMap().get(isoname);
			// store variant pos on isoform (default is null)
			isoExpectedPos.put(isoname, null);
			// if variant maps on isoform
			if (spec != null) {
				int p1 = spec.getFirstPosition();
				// store variant position on isoform
				isoExpectedPos.put(isoname, new Integer(p1));
			}
		}
		return isoExpectedPos;
	}

	private void printExpectedPosForEachIsoform(Map<String, Integer> isoExpectedPos, Annotation a) {
		for (String isoname : isoExpectedPos.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(a.getUniqueName()).append(" ");
			sb.append(isoname).append(" ");
			sb.append(isoExpectedPos.get(isoname)).append(" ");
			sb.append(a.getVariant().getOriginal()).append("->").append(a.getVariant().getVariant());
		}
	}

	private void printIsoLengthAndRangesNuCount(String isoName, String isoSeq, List<NucleotidePositionRange> ranges) {
		int isoLng = isoSeq.length();
		int nuCount = getNucleotideCount(ranges);
		boolean ok = isoLng * 3 == nuCount;
		//for (NucleotidePositionRange r: ranges)
			//System.out.println(isoName + " has masterMapping range " + r);
		//System.out.println((ok ? "OK - " : "ERROR4 - ") + isoName + " lng in nu:" + isoLng * 3 + " nuCount:" + nuCount);
	}

	private int getNucleotideCount(List<NucleotidePositionRange> ranges) {
		int nuCount = 0;
		for (NucleotidePositionRange r : ranges) {
			int cnt = r.getUpper() - r.getLower() + 1;
			nuCount += cnt;
		}
		return nuCount;
	}

	private String getSequenceWithHighlighedPos(String seq, int pos) {
		// first aa has position 1
		StringBuilder sb = new StringBuilder(seq);
		sb.insert(pos - 1, '(').insert(pos + 1, ')');
		return sb.toString();
	}
	
	private BufferedWriter log=null;
	private void openLogger(String filename) throws Exception {
		this.log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
	}
	private void log(String s) throws IOException {
		if (log!=null) {
			log.write(s);
			log.write("\n");
			log.flush();
		}
	}
	private void closeLogger() throws IOException {
		log.close();
	}

}
