package org.nextprot.api.core.service;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.impl.GenomicMappingServiceImpl;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

/**
 * Class used for testing Genomic Mapping DAO
 * 
 * @author dteixeira
 */
//@DatabaseSetup(value = "GenomicMappingP41134Test.xml", type = DatabaseOperation.INSERT)
public class GenomicMappingP41134ServiceTest {

	@InjectMocks
	private GenomicMappingService genomicMappingService = new GenomicMappingServiceImpl();

	@Mock
	private GeneDAO geneDAO;

	@Mock
	private IsoformService isoformService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void verifyGetTheGenomicMappingForP41134FromService() throws Exception {

		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn("NX_P12345-1");

		when(geneDAO.findGenomicMappingByEntryName("NX_P41134")).thenReturn(Lists.newArrayList(new GenomicMapping()));
		when(isoformService.findIsoformsByEntryName("NX_P41134")).thenReturn(Lists.newArrayList(isoform));

		genomicMappingService.findGenomicMappingsByEntryName("NX_P41134");

		verify(geneDAO).findGenomicMappingByEntryName("NX_P41134");
		verify(isoformService).findIsoformsByEntryName("NX_P41134");
		verify(geneDAO).getIsoformMappingsByIsoformName(anyListOf(String.class));
	}

	@Ignore
	@Test
	public void shouldGetTheGenomicMappingForP41134FromService() throws Exception {
		genomicMappingService.findGenomicMappingsByEntryName("NX_P41134");
	}
}
