package org.nextprot.api.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.CodonNucleotideIndices;
import org.nextprot.api.commons.utils.CodonNucleotidePositions;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.commons.utils.PropagatorCore;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.EntryBuilderService;	
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "cache" })
public class PropagatorIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	//NX_O00555 causes an error
	
	//@Test
	public void testPropagationForVariantsOfAllEntries() throws Exception {
		Set<String> acs = masterIdentifierService.findUniqueNames();
		int cnt=0;
		boolean working=false;
		for (String ac: acs) {
			cnt++;
			if (working) {
				System.out.println("--- START testing propagation for variants of entry no. " + cnt + ":" + ac);
				int errorCnt = getErrorsDuringPropagationOnVariantsOfSingleEntry(ac);
				System.out.println("--- END   testing propagation for variants of entry no. " + cnt + ":" + ac + (errorCnt==0 ? ": OK":": with " +errorCnt + " ERRORs"));;
				Assert.assertEquals(0, errorCnt);
			}
			if (ac.equals("NX_P78324")) working=true; // start just after last known error
		}
	}

	
		
	
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

	public int getErrorsDuringPropagationOnVariantsOfSingleEntry(String entry_ac) throws Exception {

		PropagatorCore.debug = false;

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entry_ac).withEverything());

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
				} else { System.out.println("Other variant:" + a.getUniqueName());	otherCount++;
				}

				Map<String, Integer> isoExpectedPos = getExpectedPosForEachIsoform(entry, a);
				printExpectedPosForEachIsoform(isoExpectedPos, a);

				// now start checking the propagator
				Propagator propagator = new Propagator(entry);
				boolean errorOnVariant = false;

				for (String iso1name : isoExpectedPos.keySet()) {
					Integer iso1ExpectedPos = isoExpectedPos.get(iso1name);
					Isoform iso1 = propagator.getIsoformByName(iso1name);
					if (iso1ExpectedPos != null) {

						CodonNucleotidePositions nuPos = propagator.getMasterCodonNucleotidesPositions(iso1ExpectedPos, iso1);

						if (!nuPos.isValid()) {
							errorOnVariant = true;
							System.out.println("ERROR1: codon positions not found for " + iso1name + " for variant at position: " + iso1ExpectedPos);
							continue;
						}

						printIsoLengthAndRangesNuCount(iso1.getSequence(), iso1.getMasterMapping());
						System.out.println("Starting variant propagation from isoform " + iso1name + " at position " + iso1ExpectedPos);
						System.out.println(getSequenceWithHighlighedPos(iso1.getSequence(), iso1ExpectedPos));
	
						for (Isoform iso2 : entry.getIsoforms()) {
							String iso2name = iso2.getUniqueName();
							if (iso2name.equals(iso1name))	continue;
	
							CodonNucleotideIndices nuIdx = propagator.getMasterCodonNucleotidesIndices(nuPos, iso2);
	
							Integer iso2ActualPos = nuIdx.getAminoAcidPosition();
							Integer iso2ExpectedPos = isoExpectedPos.get(iso2name);
							System.out.println("Variant " + a.getUniqueName() + " position on isoform " + iso2name + " is "	+ iso2ActualPos);
							printIsoLengthAndRangesNuCount(iso2.getSequence(), iso2.getMasterMapping());
							if (iso2ExpectedPos != null) System.out.println("Expected:" + getSequenceWithHighlighedPos(iso2.getSequence(), iso2ExpectedPos));
							if (iso2ActualPos != null)	System.out.println("Actual  :"	+ getSequenceWithHighlighedPos(iso2.getSequence(), iso2ActualPos));
							
							if (iso2ActualPos == null && iso2ExpectedPos == null) {
								// OK
							} else if (iso2ActualPos == null || iso2ExpectedPos == null) {
								errorOnVariant = true;
								System.out.println("ERROR2: variant position on isoform " + iso2name + " is "
										+ iso2ActualPos + ", expected " + iso2ExpectedPos);
							} else if (!iso2ActualPos.equals(iso2ExpectedPos)) {
								errorOnVariant = true;
								System.out.println("ERROR3: variant position on isoform " + iso2name + " is "
										+ iso2ActualPos + ", expected " + iso2ExpectedPos);
							}
						}
					}
				}
				if (errorOnVariant)	errorCount++;
				if (errorOnVariant)	break;
			}
		}
		System.out.println("Summary " + entry.getUniqueName());
		System.out.println("insCount:" + insCount);
		System.out.println("delCount:" + delCount);
		System.out.println("subCount:" + subCount);
		System.out.println("otherCount:" + otherCount);
		System.out.println("errorCount:" + errorCount);
		return errorCount;
	}

	@Test
	public void test1() {
		System.out.println("pos 3:" + getSequenceWithHighlighedPos("12345", 3));
		System.out.println("pos 1:" + getSequenceWithHighlighedPos("12345", 1));
		System.out.println("pos 5:" + getSequenceWithHighlighedPos("12345", 5));

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
			System.out.println(sb.toString());
		}
	}

	private void printIsoLengthAndRangesNuCount(String isoSeq, List<NucleotidePositionRange> ranges) {
		int isoLng = isoSeq.length();
		int nuCount = getNucleotideCount(ranges);
		boolean ok = isoLng * 3 == nuCount;
		System.out.println((ok ? "OK - " : "ERROR4 - ") + "Iso lng in nu:" + isoLng * 3 + " nuCount:" + nuCount);
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
}
