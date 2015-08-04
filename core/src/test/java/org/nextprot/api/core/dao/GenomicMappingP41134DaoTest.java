package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.core.domain.Exon;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.TranscriptMapping;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class used for testing Genomic Mapping DAO
 * 
 * @author dteixeira
 */
@DatabaseSetup(value = "GenomicMappingP41134Test.xml", type = DatabaseOperation.INSERT)
public class GenomicMappingP41134DaoTest extends CoreUnitBaseTest {

	@Autowired	GeneDAO geneDAO;

	@Test
	public void shouldGetAGenomicMapping() throws Exception {
		List<GenomicMapping> gms = geneDAO.findGenomicMappingByEntryName("NX_P41134");
		assertEquals(gms.size(), 1);
		GenomicMapping gm = gms.get(0);
		assertEquals(270075, gm.getGeneSeqId());
		assertEquals("Ensembl", gm.getDatabase());
		assertEquals("ENSG00000125968", gm.getAccession());
	}

	@Test
	public void shouldGetTranscriptMapping() throws Exception {
		List<TranscriptMapping> tms = geneDAO.findTranscriptsByIsoformNames(Arrays.asList("NX_P41134-1", "NX_P41134-2"));
		assertEquals(2, tms.size());
	}

	@Test
	public void shouldGetExons() throws Exception {
		List<Exon> exs = geneDAO.findExonsAlignedToTranscriptOfGene("NX_ENST00000376105", "NX_ENSG00000125968");
		assertEquals(exs.size(), 1);
	}

	@Test
	public void shouldGetGenomicMappingForP41134FromDao() throws Exception {
		List<GenomicMapping> gms = geneDAO.findGenomicMappingByEntryName("NX_P41134");
		assertTrue(gms.size() > 0);
	}
}
