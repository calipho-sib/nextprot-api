package org.nextprot.api.core.service;

import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;

public abstract class AnnotationBuilderBastUnitTest {

	@Mock
	protected TerminologyService terminologyService;
	@Mock
	protected PublicationService publicationService;
	@Mock
	protected MainNamesService mainNamesService;
    @Mock
    protected DbXrefService dbXrefService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		CvTerm cvterm = new CvTerm();
		cvterm.setName("eco-name-1");
		cvterm.setOntology("eco-ontology-cv");
		cvterm.setDescription("some description");

		Mockito.when(terminologyService.findCvTermByAccession(Matchers.anyString())).thenReturn(cvterm);
		Mockito.when(terminologyService.findCvTermByAccessionOrThrowRuntimeException(Matchers.anyString())).thenReturn(cvterm);

		Publication pub = new Publication();
		pub.setId(999);

		Mockito.when(publicationService.findPublicationByDatabaseAndAccession("PubMed", "000")).thenReturn(null);
		Mockito.when(publicationService.findPublicationByDatabaseAndAccession("PubMed", "123")).thenReturn(pub);
		
		//unused in tests yet
		Mockito.when(mainNamesService.findIsoformOrEntryMainName()).thenReturn(null);

	}
	
	protected abstract StatementAnnotationBuilder newAnnotationBuilder();

}
