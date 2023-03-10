package org.nextprot.api.etl.statement;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl;
import org.nextprot.api.etl.service.transform.impl.StatementTransformerServiceImpl;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public abstract class StatementETLBaseUnitTest {

	@Mock private IsoformService isoformService;

	@Mock protected IsoformMappingService isoformMappingServiceMocked;

	protected StatementETLServiceImpl statementETLServiceMocked = null;

	protected StatementTransformerServiceImpl transformerMockedService = null;
	
	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		mockIsoMapperService();

		List<Isoform> isoformsNX_P43246 = Arrays.asList(mockIsoform("NX_P43246-1", "Iso 1", true), mockIsoform("NX_P43246-2", "Iso 2", true));
		List<Isoform> isoformsNX_P52701 = Arrays.asList(mockIsoform("NX_P52701-1", "GTBP-N", true), mockIsoform("NX_P52701-2", "GTBP-alt", false), mockIsoform("NX_P52701-3", "Iso 3", false), mockIsoform("NX_P52701-4", "Iso 4", false));
		List<Isoform> isoformsNX_Q15858 = Arrays.asList(mockIsoform("NX_Q15858-1", "Iso 1", true), mockIsoform("NX_Q15858-2", "Iso 2", false), mockIsoform("NX_Q15858-3", "Iso 3", false), mockIsoform("NX_Q15858-4", "Iso 4", false));
		List<Isoform> isoformsNX_P12111 = Arrays.asList(mockIsoform("NX_P12111-1", "Iso 1", true), mockIsoform("NX_P12111-2", "Iso 2", false), mockIsoform("NX_P12111-3", "Iso 3", false), mockIsoform("NX_P12111-4", "Iso 4", false));
		List<Isoform> isoformsNX_Q9Y4L1 = Arrays.asList(mockIsoform("NX_Q9Y4L1-1", "Iso 1", true), mockIsoform("NX_Q9Y4L1-2", "Iso 2", false));
		List<Isoform> isoformsNX_Q14524 = Arrays.asList(mockIsoform("NX_Q14524-1", "Iso 1", true),
				mockIsoform("NX_Q14524-2", "Iso 2", false),
				mockIsoform("NX_Q14524-3", "Iso 3", false),
				mockIsoform("NX_Q14524-4", "Iso 4", false),
				mockIsoform("NX_Q14524-5", "Iso 5", false),
				mockIsoform("NX_Q14524-6", "Iso 6", false));

		Mockito.when(isoformService.findIsoformsByEntryName("NX_P43246")).thenReturn(isoformsNX_P43246);
		Mockito.when(isoformService.findIsoformsByEntryName("NX_P52701")).thenReturn(isoformsNX_P52701);
		Mockito.when(isoformService.findIsoformsByEntryName("NX_Q15858")).thenReturn(isoformsNX_Q15858);
		Mockito.when(isoformService.findIsoformsByEntryName("NX_P12111")).thenReturn(isoformsNX_P12111);
		Mockito.when(isoformService.findIsoformsByEntryName("NX_Q9Y4L1")).thenReturn(isoformsNX_Q9Y4L1);
		Mockito.when(isoformService.findIsoformsByEntryName("NX_Q14524")).thenReturn(isoformsNX_Q14524);

		statementETLServiceMocked = new StatementETLServiceImpl();
		
		transformerMockedService = new StatementTransformerServiceImpl();
	}

	private void mockIsoMapperService() {

		{
			SingleFeatureQuerySuccessImpl result1 = Mockito.mock(SingleFeatureQuerySuccessImpl.class);
			Mockito.when(result1.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data1 = new HashMap<>();

			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 859, 859, 2665, 2667, true, "SCN9A-iso1-p.Ile859Thr"),
					new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 859, 859, 2665, 2667, false, "SCN9A-iso2-p.Ile859Thr"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 848, 848, 2665, 2667, false, "SCN9A-iso3-p.Ile848Thr"),
					new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 848, 848, 2665, 2667, false, "SCN9A-iso4-p.Ile848Thr")).forEach(r -> data1.put(r.getIsoformAccession(), r));

			Mockito.when(result1.getData()).thenReturn(data1);
			Mockito.when(isoformMappingServiceMocked.propagateFeature(new SingleFeatureQuery("SCN9A-iso3-p.Ile848Thr", "variant", "NX_Q15858"))).thenReturn(result1);
		}
		/////////////////////////

		{

			SingleFeatureQuerySuccessImpl result2 = Mockito.mock(SingleFeatureQuerySuccessImpl.class);
			Mockito.when(result2.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data2 = new HashMap<>();

			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 943, 943, 2917, 2919, true, "SCN9A-iso1-p.Met943Leu"),
					new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 943, 943, 2917, 2919, false, "SCN9A-iso2-p.Met943Leu"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 932, 932, 2917, 2919, false, "SCN9A-iso3-p.Met932Leu"),
					new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 932, 932, 2917, 2919, false, "SCN9A-iso4-p.Met932Leu")).forEach(r -> data2.put(r.getIsoformAccession(), r));

			Mockito.when(result2.getData()).thenReturn(data2);
			Mockito.when(isoformMappingServiceMocked.propagateFeature(new SingleFeatureQuery("SCN9A-iso3-p.Met932Leu", "variant", "NX_Q15858"))).thenReturn(result2);

		}

		/////////////////////////

		{

			SingleFeatureQuerySuccessImpl result2 = Mockito.mock(SingleFeatureQuerySuccessImpl.class);
			Mockito.when(result2.isSuccess()).thenReturn(true);
			Map<String, IsoformFeatureResult> data2 = new HashMap<>();

			// Let's say this one can not be propagated on 2 and 4
			Arrays.asList(new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 1002, 1002, 3094, 3096, true, "SCN9A-iso1-p.Val1002Leu"),
					new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 991, 991, 3094, 3096, false, "SCN9A-iso3-p.Val991Leu")).forEach(r -> data2.put(r.getIsoformAccession(), r));

			Mockito.when(result2.getData()).thenReturn(data2);
			Mockito.when(isoformMappingServiceMocked.propagateFeature(new SingleFeatureQuery("SCN9A-iso3-p.Val991Leu", "variant", "NX_Q15858"))).thenReturn(result2);

		}

	}

	public static Isoform mockIsoform(String accession, String name, boolean canonical) {

		Isoform isoform = Mockito.mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn(accession);
		when(isoform.getIsoformAccession()).thenReturn(accession);
		when(isoform.isCanonicalIsoform()).thenReturn(canonical);

		EntityName entityName = Mockito.mock(EntityName.class);
		when(entityName.getName()).thenReturn(name);

		when(isoform.getMainEntityName()).thenReturn(entityName);

		return isoform;
	}
}
