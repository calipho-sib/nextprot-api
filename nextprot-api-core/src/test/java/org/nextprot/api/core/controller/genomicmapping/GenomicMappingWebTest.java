package org.nextprot.api.core.controller.genomicmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.MVCDBUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Class used for testing Genomic Mapping controller
 * 
 * @author dteixeira
 */
@DatabaseSetup(value = "GenomicMappingTest.xml", type = DatabaseOperation.INSERT)
@Ignore
public class GenomicMappingWebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldGetAMonoExon() throws Exception {
		this.mockMvc
				.perform(get("/entry/NX_P41134/genomic-mappings.xml"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon//@codingStatus").string("MONO"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon//@accession").string("ENSE00001469386"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/positionOnGene//@first").string("7"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/positionOnGene//@last").string("1228"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[1]//@rank").string("first"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[1]//@sequence").string("M"))
				.andExpect(
						xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[1]//@sequencePosition").string(
								"1"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[1]//@phase").string("0"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[2]//@rank").string("last"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[2]//@sequence").string("H"))
				.andExpect(
						xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[2]//@sequencePosition").string(
								"149"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[2]/transcriptMappings/transcriptMapping/exonsComposition/exon/aminoAcid[2]//@phase").string("0"));
	}

	@Test
	public void shouldGetAStopOnlyExon() throws Exception {
		this.mockMvc
				.perform(get("/entry/NX_Q96M20/genomic-mappings.xml"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[3]/transcriptMappings/transcriptMapping/exonsComposition/exon[11]//@codingStatus").string("STOP_ONLY"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[3]/transcriptMappings/transcriptMapping/exonsComposition/exon[11]//@rank").string("10"))
				.andExpect(
						xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[3]/transcriptMappings/transcriptMapping/exonsComposition/exon[11]//@accession").string("ENSE00002283130"))
				.andExpect(
						xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[3]/transcriptMappings/transcriptMapping/exonsComposition/exon[11]/positionOnGene//@first")
								.string("61768"))
				.andExpect(
						xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[3]/transcriptMappings/transcriptMapping/exonsComposition/exon[11]/positionOnGene//@last").string("62080"));
	}

	@Test
	public void shouldGetCorrectAAsForExons() throws Exception {
		this.mockMvc.perform(get("/entry/NX_P59103/genomic-mappings.xml")).andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[1]//@uniqueName").string("NX_P59103-1"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[1]//@isoMainName").string("Iso 1"))
				.andExpect(xpath(suf(1, 1, 0, 0) + "//@database").string("Ensembl")).andExpect(xpath(suf(1, 1, 0, 0) + "//@accession").string("ENST00000375936"))
				.andExpect(xpath(suf(1, 1, 0, 0) + "//@proteinID").string("ENSP00000365103")).andExpect(xpath(suf(1, 1, 1, 0) + "//@rank").string("0"))
				.andExpect(xpath(suf(1, 1, 1, 0) + "//@codingStatus").string("START")).andExpect(xpath(suf(1, 1, 1, 0) + "//@accession").string("ENSE00001942959"))
				.andExpect(xpath(suf(1, 1, 1, 0) + "/positionOnGene//@first").string("377")).andExpect(xpath(suf(1, 1, 1, 0) + "/positionOnGene//@last").string("466"))
				.andExpect(xpath(suf(1, 1, 1, 1) + "//@rank").string("first")).andExpect(xpath(suf(1, 1, 1, 1) + "//@sequence").string("M"))
				.andExpect(xpath(suf(1, 1, 1, 1) + "//@sequencePosition").string("1")).andExpect(xpath(suf(1, 1, 1, 1) + "//@phase").string("0"))
				.andExpect(xpath(suf(1, 1, 1, 2) + "//@rank").string("last")).andExpect(xpath(suf(1, 1, 1, 2) + "//@sequence").string("R"))
				.andExpect(xpath(suf(1, 1, 1, 2) + "//@sequencePosition").string("15")).andExpect(xpath(suf(1, 1, 1, 2) + "//@phase").string("2"));
	}

	@Test
	public void shouldGetDifferentIsoforMainmName() throws Exception {
		this.mockMvc.perform(get("/entry/NX_P31994/genomic-mappings.xml")).andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[1]//@uniqueName").string("NX_P31994-1"))
				.andExpect(xpath("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[1]//@isoMainName").string("IIB1"));
	}

	@Test
	public void shouldCountMultipleGenesIsoformsAndTranscripts() throws Exception {
		this.mockMvc.perform(get("/entry/NX_Q8NHW4/genomic-mappings.xml")).andExpect(xpath("genomicMappings/genomicMapping").nodeCount(2))
				.andExpect(xpath("genomicMappings/genomicMapping[1]/isoformMappings/isoformMapping").nodeCount(9))
				.andExpect(xpath("genomicMappings/genomicMapping[1]/isoformMappings/isoformMapping[1]/transcriptMappings/transcriptMapping").nodeCount(1));
	}

	private static String suf(int isoform, int transcript, int exon, int aminoAcid) {
		StringBuilder sb = new StringBuilder("/genomicMappings/genomicMapping/isoformMappings/isoformMapping[");
		sb.append(isoform);
		sb.append("]");

		if (transcript > 0) {
			sb.append("/transcriptMappings/transcriptMapping[");
			sb.append(transcript);
			sb.append("]");
		}

		if (exon > 0) {
			sb.append("/exonsComposition/exon[");
			sb.append(exon);
			sb.append("]");
		}

		if (aminoAcid > 0) {
			sb.append("/aminoAcid[");
			sb.append(aminoAcid);
			sb.append("]");
		}

		return sb.toString();
	}
	// TODO how to guarantee the order?
	// check a transcript shared

}
