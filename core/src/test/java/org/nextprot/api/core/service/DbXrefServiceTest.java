package org.nextprot.api.core.service;

import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.service.impl.DbXrefServiceImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@DatabaseSetup(value = "DbXrefServiceTest.xml", type = DatabaseOperation.INSERT)
public class DbXrefServiceTest {

	private FlatXmlDataSetExtractor flatXmlDataSetExtractor;

	@InjectMocks
	private DbXrefService xrefService = new DbXrefServiceImpl();

	@Mock
	private DbXrefDao dbXRefDao;

	@Mock
	private PeptideNamesService peptideNamesService;

	@Mock
	private AntibodyResourceIdsService antibodyResourceIdsService;

    @Mock
    private StatementService statementService;

	@Before
	public void init() throws FileNotFoundException, DataSetException {

		flatXmlDataSetExtractor = new FlatXmlDataSetExtractor(AnnotationServiceTest.class.getResource("DbXrefServiceTest.xml").getFile());

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void verifyFindDbXrefsByPublicationIds() {
		xrefService.findDbXRefByPublicationId(100L);
		verify(dbXRefDao).findDbXRefsByPublicationId(100L);
	}

	@Ignore
	@Test
	public void testFindDbXrefsByPublicationIds() {
		List<PublicationDbXref> xrefs = this.xrefService.findDbXRefByPublicationId(100L);
		assertEquals(1, xrefs.size());
		assertEquals("15923218", xrefs.get(0).getAccession());
	}

	@Test
	public void verifyFindDbXrefsByMaster()  {
		xrefService.findDbXrefsByMaster("NX_P12345");

		verify(peptideNamesService).findAllPeptideNamesByMasterId(anyString());
		verify(dbXRefDao, times(0)).findPeptideXrefs(anyListOf(String.class));
		verify(dbXRefDao).findEntryAnnotationsEvidenceXrefs("NX_P12345");
		verify(dbXRefDao).findEntryAttachedXrefs("NX_P12345");
		verify(dbXRefDao).findEntryIdentifierXrefs("NX_P12345");
		verify(dbXRefDao).findEntryInteractionXrefs("NX_P12345");
	}

	@Ignore
	@Test
	public void testFindDbXrefsByMaster()  {
		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_P12345");
	
		assertEquals(1, xrefs.size());
		assertEquals(1, xrefs.get(0).getProperties().size());
		assertEquals("money", xrefs.get(0).getProperties().get(0).getName());
	}

	private static List<DbXref> extractDbXRefs(long id, FlatXmlDataSetExtractor extractor) throws DataSetException {

		FlatXmlDataSetExtractor.Factory<DbXref> factory = new FlatXmlDataSetExtractor.Factory<DbXref>() {

			@Override
			public DbXref create() {
				return new DbXref();
			}

			@Override
			public void setField(DbXref dbxref, String key, String value) {

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

		List<DbXref> data = new ArrayList<>();

		for (DbXref xref : extractor.extractDataList("db_xrefs", factory, "accession", "resource_id")) {

			if (xref.getDbXrefId() == id)
				data.add(xref);
		}

		return data;
	}
}
