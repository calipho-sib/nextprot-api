package org.nextprot.api.core.service;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.service.impl.PeptideMappingServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

//@DatabaseSetup(value = "PeptideMappingServiceTest.xml", type = DatabaseOperation.INSERT)
public class PeptideMappingServiceTest {

	@InjectMocks
	private PeptideMappingService peptideMappingService = new PeptideMappingServiceImpl();

	@Mock
	private MasterIdentifierService masterIdentifierService;

	@Mock
	private PeptideMappingDao peptideMappingDao;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void verifyFindNaturalPeptideMappingByMasterId() {

		PeptideMapping mapping = mock(PeptideMapping.class);
		when(mapping.getPeptideUniqueName()).thenReturn("COOLIO");

		when(peptideMappingDao.findNaturalPeptidesByMasterId(anyLong())).thenReturn(Lists.newArrayList(mapping));

		peptideMappingService.findNaturalPeptideMappingByMasterId(596889L);

		verify(peptideMappingDao).findNaturalPeptidesByMasterId(596889L);
		verify(peptideMappingDao, times(0)).findSyntheticPeptidesByMasterId(596889L);
		verify(peptideMappingDao).findNaturalPeptideEvidences(Lists.newArrayList("COOLIO"));
		verify(peptideMappingDao).findPeptideProperties(Lists.newArrayList("COOLIO"));
	}

	@Test
	public void verifyFindNaturalPeptideMappingByMasterId2() {

		when(peptideMappingDao.findNaturalPeptidesByMasterId(anyLong())).thenReturn(new ArrayList<PeptideMapping>());

		peptideMappingService.findNaturalPeptideMappingByMasterId(596889L);

		verify(peptideMappingDao).findNaturalPeptidesByMasterId(596889L);
		verify(peptideMappingDao, times(0)).findSyntheticPeptidesByMasterId(596889L);
		verify(peptideMappingDao, times(0)).findNaturalPeptideEvidences(Lists.newArrayList("COOLIO"));
		verify(peptideMappingDao, times(0)).findPeptideProperties(Lists.newArrayList("COOLIO"));
	}

	@Test
	public void verifyFindSyntheticPeptideMappingByMasterId() {

		PeptideMapping mapping = mock(PeptideMapping.class);
		when(mapping.getPeptideUniqueName()).thenReturn("SYNTH");

		when(peptideMappingDao.findSyntheticPeptidesByMasterId(anyLong())).thenReturn(Lists.newArrayList(mapping));

		peptideMappingService.findSyntheticPeptideMappingByMasterId(596889L);

		verify(peptideMappingDao).findSyntheticPeptidesByMasterId(596889L);
		verify(peptideMappingDao, times(0)).findNaturalPeptidesByMasterId(596889L);
		verify(peptideMappingDao).findSyntheticPeptideEvidences(Lists.newArrayList("SYNTH"));
		verify(peptideMappingDao).findPeptideProperties(Lists.newArrayList("SYNTH"));
	}

	@Ignore
	@Test
	public void testFindPeptideMappingByMasterId() {
		List<PeptideMapping> mapping = this.peptideMappingService.findNaturalPeptideMappingByMasterId(596889L);
		assertEquals(1, mapping.size());
		assertEquals("NX_PEPT12345678", mapping.get(0).getPeptideUniqueName());
		assertEquals(1, mapping.get(0).getEvidences().size());
		assertEquals("789654", mapping.get(0).getEvidences().get(0).getAccession());
	}

	@Test
	public void verifyFindPeptideMappingByUniqueName() {

		peptideMappingService.findNaturalPeptideMappingByMasterUniqueName("NX_P12345");

		verify(masterIdentifierService).findIdByUniqueName("NX_P12345");
	}

	@Ignore
	@Test
	public void testFindPeptideMappingByUniqueName() {
		List<PeptideMapping> mapping = this.peptideMappingService.findNaturalPeptideMappingByMasterUniqueName("NX_P12345");
		assertEquals("NX_PEPT12345678", mapping.get(0).getPeptideUniqueName());
		assertEquals(1, mapping.get(0).getEvidences().size());
		assertEquals("789654", mapping.get(0).getEvidences().get(0).getAccession());
	}
}
