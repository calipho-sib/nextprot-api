package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.domain.IsoformPositions;
import org.nextprot.api.etl.service.StatementIsoformPositionService;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.etl.service.impl.SimpleStatementTransformerServiceTest.expectedSCN9Aiso3Met932LeuStatement;
import static org.nextprot.api.etl.service.impl.SimpleStatementTransformerServiceTest.expectedSCN9Aiso3Val991LeuStatement;
import static org.nextprot.api.etl.statement.StatementETLBaseUnitTest.mockIsoform;
import static org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class StatementIsoformPositionServiceTest {

	@InjectMocks
	private StatementIsoformPositionService statementIsoformPositionService = new StatementIsoformPositionServiceImpl();

	@Mock
	private IsoformService isoformService;

	@Mock
	private IsoformMappingService isoformMappingService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
		mockIsoformService();
		mockIsoformMappingService();
	}

	@Test
	public void shouldComputeCorrectTargetIsoformsForVariants() {

		Statement subjectStatement = new StatementBuilder()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Ile848Thr")
				.addField(ANNOTATION_CATEGORY, "variant")
				.build();

		IsoformPositions isoformPositions =
				statementIsoformPositionService.computeIsoformPositionsForNormalAnnotation(subjectStatement);

		String targetIsoformsString = isoformPositions.getTargetIsoformSet().serializeToJsonString();

		Set<TargetIsoformStatementPosition> targetIsoforms = TargetIsoformSet.deSerializeFromJsonString(targetIsoformsString);

		Assert.assertEquals(targetIsoforms.size(), 4);
		TargetIsoformStatementPosition pos1 = targetIsoforms.stream().filter(ti -> ti.getIsoformAccession().equals("NX_Q15858-1")).collect(Collectors.toList()).get(0);
		Assert.assertEquals(859, (int) pos1.getBegin());
		Assert.assertEquals("SCN9A-iso1-p.Ile859Thr", pos1.getName());
	}

	@Test
	public void shouldComputeCorrectTargetIsoformsForProteoformAnnotations() {

		List<Statement> transformedSubjects = Arrays.asList(
				expectedSCN9Aiso3Met932LeuStatement(),
				expectedSCN9Aiso3Val991LeuStatement()
		);

		Set<TargetIsoformStatementPosition> result =
				statementIsoformPositionService.computeTargetIsoformsForProteoformAnnotation(
						transformedSubjects, true, "NX_Q15858-3");

		Assert.assertEquals(1, result.size());
		Assert.assertEquals("SCN9A-iso3-p.Met932Leu + SCN9A-iso3-p.Val991Leu", result.iterator().next().getName());
		Assert.assertEquals(result.iterator().next().getSpecificity(), IsoTargetSpecificity.SPECIFIC.name());
	}

	@Test
	public void shouldComputeCorrectTargetIsoformsForProteoformAnnotationsNonIsoSpec() {

		List<Statement> transformedSubjects = Arrays.asList(
				expectedSCN9Aiso3Met932LeuStatement(),
				expectedSCN9Aiso3Val991LeuStatement()
		);

		Set<TargetIsoformStatementPosition> result =
				statementIsoformPositionService.computeTargetIsoformsForProteoformAnnotation(
						transformedSubjects, false, null);

		Assert.assertEquals( 2, result.size()); //Because SCN9A-iso1-p.Val991Leu can only be propagated on 1 and 3
		Assert.assertEquals("SCN9A-iso1-p.Met943Leu + SCN9A-iso1-p.Val1002Leu", result.iterator().next().getName());
		Assert.assertEquals(result.iterator().next().getSpecificity(), IsoTargetSpecificity.UNKNOWN.name());
	}

	private SingleFeatureQuerySuccessImpl mockSingleFeatureQuerySuccess(IsoformFeatureResult... isoformFeatureResults) {

		SingleFeatureQuerySuccessImpl result = Mockito.mock(SingleFeatureQuerySuccessImpl.class);
		Mockito.when(result.isSuccess()).thenReturn(true);
		Map<String, SingleFeatureQuerySuccessImpl.IsoformFeatureResult> data = new HashMap<>();

		for (IsoformFeatureResult isoformFeatureResult : isoformFeatureResults) {
			data.put(isoformFeatureResult.getIsoformAccession(), isoformFeatureResult);
		}

		Mockito.when(result.getData()).thenReturn(data);

		return result;
	}

	private void mockIsoformMappingService() {

		List<SingleFeatureQuery> queries = Arrays.asList(
				SingleFeatureQuery.variant("SCN9A-iso3-p.Ile848Thr","NX_Q15858"),
				SingleFeatureQuery.variant("SCN9A-iso3-p.Met932Leu","NX_Q15858"),
				SingleFeatureQuery.variant("SCN9A-iso3-p.Val991Leu","NX_Q15858")
		);

		List<SingleFeatureQuerySuccessImpl> results = Arrays.asList(
				mockSingleFeatureQuerySuccess(
						new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 859, 859, 2665, 2667, true, "SCN9A-iso1-p.Ile859Thr"),
						new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 859, 859, 2665, 2667, false, "SCN9A-iso2-p.Ile859Thr"),
						new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 848, 848, 2665, 2667, false, "SCN9A-iso3-p.Ile848Thr"),
						new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 848, 848, 2665, 2667, false, "SCN9A-iso4-p.Ile848Thr")
				),
				mockSingleFeatureQuerySuccess(
						new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 943, 943, 2917, 2919, true, "SCN9A-iso1-p.Met943Leu"),
						new IsoformFeatureResult("NX_Q15858-2", "Iso 2", 943, 943, 2917, 2919, false, "SCN9A-iso2-p.Met943Leu"),
						new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 932, 932, 2917, 2919, false, "SCN9A-iso3-p.Met932Leu"),
						new IsoformFeatureResult("NX_Q15858-4", "Iso 4", 932, 932, 2917, 2919, false, "SCN9A-iso4-p.Met932Leu")
				),
				mockSingleFeatureQuerySuccess(
						new IsoformFeatureResult("NX_Q15858-1", "Iso 1", 1002, 1002, 3094, 3096, true, "SCN9A-iso1-p.Val1002Leu"),
						new IsoformFeatureResult("NX_Q15858-3", "Iso 3", 991, 991, 3094, 3096, false, "SCN9A-iso3-p.Val991Leu")
				)
		);


		for (int i=0 ; i<queries.size() ; i++) {

			Mockito.when(isoformMappingService.propagateFeature(queries.get(i))).thenReturn(results.get(i));
		}
	}

	private void mockIsoformService() {

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
	}
}