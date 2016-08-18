package com.nextprot.api.annotation.builder;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;

public abstract class AnnotationBuilderBastUnitTest {

	@Mock
	protected TerminologyService terminologyService;
	@Mock
	protected PublicationService publicationService;

	@Before
	public void init() throws FileNotFoundException, DataSetException {

		MockitoAnnotations.initMocks(this);

		CvTerm cvterm = new CvTerm();
		cvterm.setName("eco-name-1");
		cvterm.setOntology("eco-ontology-cv");
		cvterm.setDescription("some description");

		Mockito.when(terminologyService.findCvTermByAccession(Matchers.anyString())).thenReturn(cvterm);

		Publication pub = new Publication();
		pub.setId(999);

		Mockito.when(publicationService.findPublicationByDatabaseAndAccession("PubMed", "000")).thenReturn(Arrays.asList()); //Return an empty list if not found
		Mockito.when(publicationService.findPublicationByDatabaseAndAccession("PubMed", "123")).thenReturn(Arrays.asList(pub));

	}
	
	protected abstract AnnotationBuilder newAnnotationBuilder();

}
