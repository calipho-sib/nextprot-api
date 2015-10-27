package org.nextprot.api.core.service.impl;

import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.service.AntibodyMappingService;

import java.io.FileNotFoundException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AntibodyMappingServiceTest {

    @InjectMocks
    private AntibodyMappingService antibodyMappingService= new AntibodyMappingServiceImpl();

    @Mock
    private MasterIdentifierService masterIdentifierService;

    @Mock
    private AntibodyMappingDao antibodyMappingDao;

    @Before
    public void init() throws FileNotFoundException, DataSetException {

        MockitoAnnotations.initMocks(this);

        when(masterIdentifierService.findIdByUniqueName("NX_P06213")).thenReturn(636535L);
    }

	@Test
	public void testFindAntibodyMappingByMasterId() {

        this.antibodyMappingService.findAntibodyMappingAnnotationsByUniqueName("NX_P06213");

        verify(masterIdentifierService).findIdByUniqueName("NX_P06213");
        verify(antibodyMappingDao).findAntibodyMappingAnnotationsById(636535L);
	}
}
