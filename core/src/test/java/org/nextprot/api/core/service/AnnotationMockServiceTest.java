package org.nextprot.api.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.BioPhyChemPropsDao;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.impl.AnnotationServiceImpl;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author dteixeira
 * @author fnikitin
 */
public class AnnotationMockServiceTest {

	private FlatXmlDataSetExtractor flatXmlDataSetExtractor;

	@InjectMocks
	private AnnotationService annotationService = new AnnotationServiceImpl();

	@Mock
	private MasterIdentifierService masterIdentifierService;

	@Mock
	private AnnotationDAO annotationDAO;

	@Mock
	private DbXrefDao dbXrefDao;

	@Mock
	private DbXrefService dbXrefService;

	@Mock
	private IsoformService isoformService;

	@Mock 
	private InteractionService interactionService;

	@Mock 
	private PeptideMappingService peptideMappingService;

	@Mock
	private IsoformDAO isoformDAO;

	@Mock
	private MdataService mdataService;

	@Mock
	private BioPhyChemPropsDao bioPhyChemPropsDao;

	@Mock
	private AntibodyMappingService antibodyMappingService;

	@Mock
	private StatementService statementService;
	
	@Mock
	private ExperimentalContextDictionaryService experimentalContextDictionaryService;

	@Mock
	private EntityNameService entityNameService;

	@Before
	public void init() throws FileNotFoundException, DataSetException {

		flatXmlDataSetExtractor = new FlatXmlDataSetExtractor(AnnotationMockServiceTest.class.getResource("AnnotationMVCTest.xml").getFile());

		MockitoAnnotations.initMocks(this);

        EntityName entityName = new EntityName();
        entityName.setMain(true);
        entityName.setName("spongebob");

		when(masterIdentifierService.findUniqueNames()).thenReturn(Sets.newHashSet("NX_P12345, NX_P10000"));
		when(entityNameService.findNamesByEntityNameClass("NX_P12345", Overview.EntityNameClass.GENE_NAMES))
                .thenReturn(Collections.singletonList(entityName));
	}

	@Test
	public void verifyAnnotationService() throws DataSetException {

		stubAnnotationDAO("NX_P12345", flatXmlDataSetExtractor, annotationDAO);

		annotationService.findAnnotations("NX_P12345");

		Mockito.verify(annotationDAO).findAnnotationsByEntryName("NX_P12345");
		Mockito.verify(interactionService).findInteractionsAsAnnotationsByEntry("NX_P12345");
		Mockito.verify(dbXrefService).findDbXrefsAsAnnotationsByEntry("NX_P12345");
	}

	@Test
	public void shouldGetTheListOfAnnotationsFromService() throws DataSetException {

		stubAnnotationDAO("NX_P12345", flatXmlDataSetExtractor, annotationDAO);

		List<Annotation> annotations = annotationService.findAnnotations("NX_P12345");
		assertEquals(6, annotations.size());
	}

	@Test
	public void shouldRemoveAnnotationEvidenceWithoutExperimentalContextForNextprotVariants() throws DataSetException {

		stubAnnotationDAO("NX_P12345", flatXmlDataSetExtractor, annotationDAO);

		List<Annotation> annotations = annotationService.findAnnotations("NX_P12345");
		List<AnnotationEvidence> annotationEvidences = annotations.stream()
				.filter(annotation -> annotation.getAnnotationId() == 15).findFirst().get().getEvidences();
		assertEquals(1, annotationEvidences.size());
	}


	private static void stubAnnotationDAO(String isoformId, FlatXmlDataSetExtractor flatXmlDataSetExtractor, AnnotationDAO annotationDAO) throws DataSetException {

		List<Annotation> annotations = extractAnnotations(flatXmlDataSetExtractor);

		// Bit of a hack, make annotation with annotation id = 15 a type VARIANT
		annotations.stream()
				.forEach(annotation -> {
					if(annotation.getAnnotationId() == 15) {
						annotation.setAnnotationCategory(AnnotationCategory.VARIANT);
					}
				});
		List<AnnotationEvidence> evidences = extractAnnotationEvidences(flatXmlDataSetExtractor);
		List<AnnotationIsoformSpecificity> specs = extractIsoformSpecificity(flatXmlDataSetExtractor);

		when(annotationDAO.findAnnotationsByEntryName(isoformId)).thenReturn(annotations);
		List<Long> annotationIds = annotations.stream()
				.map(annotation -> annotation.getAnnotationId())
				.collect(Collectors.toList());
		when(annotationDAO.findAnnotationEvidencesByAnnotationIds(annotationIds)).thenReturn(evidences);
		when(annotationDAO.findAnnotationIsoformsByAnnotationIds(Mockito.anyListOf(Long.class))).thenReturn(Lists.newArrayList(specs));
	}

	private static List<Annotation> extractAnnotations(FlatXmlDataSetExtractor extractor) throws DataSetException {

		FlatXmlDataSetExtractor.Factory<Annotation> factory = new FlatXmlDataSetExtractor.Factory<Annotation>() {

			@Override
			public Annotation create() {
				return new Annotation();
			}

			@Override
			public void setField(Annotation annotation, String key, String value) {

				switch (key) {

					case "annotation_id":
						annotation.setAnnotationId(Long.valueOf(value));
						break;
					case "unique_name":
						annotation.setUniqueName(value);
						break;
					case "description":
						annotation.setDescription(value);
						break;
					case "cv_quality_qualifier_id":
						annotation.setQualityQualifier(value);
						break;
				}
			}
		};

		return extractor.extractDataList("annotations", factory, "annotation_id", "unique_name", "description", "cv_quality_qualifier_id");
	}

	private static List<AnnotationEvidence> extractAnnotationEvidences(FlatXmlDataSetExtractor extractor) throws DataSetException{
		FlatXmlDataSetExtractor.Factory<AnnotationEvidence> factory = new FlatXmlDataSetExtractor.Factory<AnnotationEvidence>() {

			@Override
			public AnnotationEvidence create() {
				return new AnnotationEvidence();
			}

			@Override
			public void setField(AnnotationEvidence evidence, String key, String value) {
				switch (key) {

					case "assoc_id":
						evidence.setEvidenceId(Long.valueOf(value));
						break;
					case "annotation_id":
						evidence.setAnnotationId(Long.valueOf(value));
						break;
					case "assigned_by_id":
						if("1".equals(value)) {
							evidence.setAssignedBy("NextProt");
						}
						break;
					case "experimental_context_id":
						if(value != null) {
							evidence.setExperimentalContextId(Long.valueOf(value));
							break;
						}
				}
			}
		};

		return extractor.extractDataList("annotation_resource_assoc", factory, "assoc_id", "annotation_id", "assigned_by_id", "experimental_context_id");
	}

	private static List<AnnotationIsoformSpecificity> extractIsoformSpecificity(FlatXmlDataSetExtractor extractor) throws DataSetException {

		FlatXmlDataSetExtractor.Factory<AnnotationIsoformSpecificity> factory = new FlatXmlDataSetExtractor.Factory<AnnotationIsoformSpecificity>() {

			@Override
			public AnnotationIsoformSpecificity create() {
				return new AnnotationIsoformSpecificity();
			}

			@Override
			public void setField(AnnotationIsoformSpecificity spec, String key, String value) {

				switch (key) {

					case "annotation_protein_id":
						spec.setAnnotationId(Long.valueOf(value));
						break;
					case "first_pos":
						spec.setFirstPosition(Integer.valueOf(value));
						break;
					case "last_pos":
						spec.setLastPosition(Integer.valueOf(value));
				}
			}
		};

		List<AnnotationIsoformSpecificity> specs = extractor.extractDataList("protein_feature_positions", factory, "annotation_protein_id", "first_pos", "last_pos");

		Map<Long, String> ids = extractSequenceIdentifiers(extractor);
		for (AnnotationIsoformSpecificity spec : specs) {
			spec.setIsoformAccession(ids.get(spec.getAnnotationId()));
		}

		return specs;
	}

	private static class LongString {

		private long key;
		private String value;

		public long getKey() {
			return key;
		}

		public void setKey(long key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private static Map<Long, String> extractSequenceIdentifiers(FlatXmlDataSetExtractor extractor) throws DataSetException {

		FlatXmlDataSetExtractor.Factory<LongString> factory = new FlatXmlDataSetExtractor.Factory<LongString>() {

			@Override
			public LongString create() {
				return new LongString();
			}

			@Override
			public void setField(LongString keyvalue, String key, String value) {

				switch (key) {

					case "identifier_id":
						keyvalue.setKey(Long.valueOf(value));
						break;
					case "unique_name":
						keyvalue.setValue(value);
				}
			}
		};

		Map<Long, String> map = new HashMap<>();
		for (LongString keyvalue : extractor.extractDataList("sequence_identifiers", factory, "identifier_id", "unique_name")) {

			map.put(keyvalue.getKey(), keyvalue.getValue());
		}

		return map;
	}
}
