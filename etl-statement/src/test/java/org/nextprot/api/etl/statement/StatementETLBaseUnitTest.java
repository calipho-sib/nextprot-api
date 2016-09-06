package org.nextprot.api.etl.statement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.service.StatementRemoteService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;

public abstract class StatementETLBaseUnitTest {

	@Mock
	private IsoformService isoformService;

	@Mock
	private StatementRemoteService statementRemoteService;

	@Mock
	protected IsoformMappingService isoformMappingServiceMocked;

	protected StatementETLServiceImpl statementETLServiceMocked = null;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		{
			FeatureQuerySuccess result1 = Mockito.mock(FeatureQuerySuccess.class);
			Mockito.when(result1.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data1 = new HashMap<String, IsoformFeatureResult>();

			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 859, 859, 2665, 2667, true, "SCN9A-iso1-p.Ile859Thr"),
					new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 859, 859, 2665, 2667, false, "SCN9A-iso2-p.Ile859Thr"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 848, 848, 2665, 2667, false, "SCN9A-iso3-p.Ile848Thr"),
					new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 848, 848, 2665, 2667, false, "SCN9A-iso4-p.Ile848Thr")).forEach(r -> data1.put(r.getIsoformAccession(), r));

			Mockito.when(result1.getData()).thenReturn(data1);
			Mockito.when(isoformMappingServiceMocked.propagateFeature("SCN9A-iso3-p.Ile848Thr", "variant", "NX_Q15858")).thenReturn(result1);
		}
		/////////////////////////

		{

			FeatureQuerySuccess result2 = Mockito.mock(FeatureQuerySuccess.class);
			Mockito.when(result2.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data2 = new HashMap<String, IsoformFeatureResult>();

			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 943, 943, 2917, 2919, true, "SCN9A-iso1-p.Met943Leu"),
					new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 943, 943, 2917, 2919, false, "SCN9A-iso2-p.Met943Leu"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 932, 932, 2917, 2919, false, "SCN9A-iso3-p.Met932Leu"),
					new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 932, 932, 2917, 2919, false, "SCN9A-iso4-p.Met932Leu")).forEach(r -> data2.put(r.getIsoformAccession(), r));

			Mockito.when(result2.getData()).thenReturn(data2);
			Mockito.when(isoformMappingServiceMocked.propagateFeature("SCN9A-iso3-p.Met932Leu", "variant", "NX_Q15858")).thenReturn(result2);

		}

		/////////////////////////

		{

			FeatureQuerySuccess result2 = Mockito.mock(FeatureQuerySuccess.class);
			Mockito.when(result2.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data2 = new HashMap<String, IsoformFeatureResult>();

			//Let's say this one can not be propagated on 2 and 4
			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 1002, 1002, 3094, 3096, true, "SCN9A-iso1-p.Val1002Leu"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 991, 991, 3094, 3096, false, "SCN9A-iso3-p.Val991Leu")).forEach(r -> data2.put(r.getIsoformAccession(), r));

			Mockito.when(result2.getData()).thenReturn(data2);
			Mockito.when(isoformMappingServiceMocked.propagateFeature("SCN9A-iso3-p.Val991Leu", "variant", "NX_Q15858")).thenReturn(result2);

		}
		
		
		
		statementETLServiceMocked = new StatementETLServiceImpl();
		statementETLServiceMocked.setIsoformMappingService(isoformMappingServiceMocked);

	}
}
