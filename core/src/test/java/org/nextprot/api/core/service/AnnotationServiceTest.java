package org.nextprot.api.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.impl.AnnotationServiceImpl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author dteixeira
 * @author fnikitin
 */
public class AnnotationServiceTest {

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

	@Before
	public void init() throws FileNotFoundException, DataSetException {

		flatXmlDataSetExtractor = new FlatXmlDataSetExtractor(AnnotationServiceTest.class.getResource("AnnotationMVCTest.xml").getFile());
		//System.out.println(flatXmlDataSetExtractor.toString());

		MockitoAnnotations.initMocks(this);

		when(masterIdentifierService.findUniqueNames()).thenReturn(Sets.newHashSet("NX_P12345, NX_P10000"));
	}

	@Test
	public void verifyAnnotationService() throws DataSetException {

		stubAnnotationDAO("NX_P12345", flatXmlDataSetExtractor, annotationDAO);

		annotationService.findAnnotations("NX_P12345");

		Mockito.verify(annotationDAO).findAnnotationsByEntryName("NX_P12345");
		Mockito.verify(isoformService).findIsoformsByEntryName("NX_P12345");
		Mockito.verify(dbXrefService).findDbXrefsAsAnnotByEntry("NX_P12345");
		Mockito.verify(annotationDAO).createAdditionalAnnotationsFromXrefs(anyList(), anyString());
	}

	@Test
	public void shouldGetTheListOfAnnotationsFromService() throws DataSetException {

		stubAnnotationDAO("NX_P12345", flatXmlDataSetExtractor, annotationDAO);

		List<Annotation> annotations = annotationService.findAnnotations("NX_P12345");
		assertEquals(5, annotations.size());
	}

	@Ignore
	@Test
	public void shouldGetAnOrphanetAnnotationFromService() {

		List<Annotation> annotations = annotationService.findAnnotations("NX_P10000");
		assertEquals(1,annotations.size());
		Annotation a = annotations.get(0);
		assertEquals("disease",a.getCategory());
		assertEquals("Some XR_ORPHA_100021 xref disease property value", a.getDescription());
		assertEquals("GOLD", a.getQualityQualifier());
		assertEquals(1, a.getEvidences().size());
		AnnotationEvidence evi = a.getEvidences().get(0);
		assertEquals("Uniprot", evi.getAssignedBy());
		assertEquals("curated", evi.getAssignmentMethod());
		assertEquals("IC", evi.getQualifierType());
		assertEquals(false, evi.isNegativeEvidence());
		assertEquals(true, evi.isValid());
		assertEquals("XR_ORPHA_100021", evi.getResourceAccession());
		assertEquals("Orphanet", evi.getResourceDb());
	}

	private static void stubAnnotationDAO(String isoformId, FlatXmlDataSetExtractor flatXmlDataSetExtractor, AnnotationDAO annotationDAO) throws DataSetException {

		List<Annotation> annotations = extractAnnotations(flatXmlDataSetExtractor);
		List<AnnotationIsoformSpecificity> specs = extractIsoformSpecificity(flatXmlDataSetExtractor);

		when(annotationDAO.findAnnotationsByEntryName(isoformId)).thenReturn(annotations);
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
			spec.setIsoformName(ids.get(spec.getAnnotationId()));
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

	private static List<DbXref> extractDbXRefs(FlatXmlDataSetExtractor extractor) throws DataSetException {

		FlatXmlDataSetExtractor.Factory<DbXref> factory = new FlatXmlDataSetExtractor.Factory<DbXref>() {

			@Override
			public DbXref create() {
				return new DbXref();
			}

			@Override
			public void setField(DbXref dbxref, String key, String value) {

				dbxref.setDatabaseCategory("disease");

				switch (key) {

					case "accession":
						dbxref.setAccession(value);
						break;
					case "resource_id":
						dbxref.setDbXrefId(Long.valueOf(value));
						break;
				}
			}
		};

		return extractor.extractDataList("db_xrefs", factory, "accession", "resource_id");
	}
}
