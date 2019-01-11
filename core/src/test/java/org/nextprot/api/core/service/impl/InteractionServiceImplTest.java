package org.nextprot.api.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.InteractionDAO;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;

@ActiveProfiles({ "dev" })
public class InteractionServiceImplTest extends CoreUnitBaseTest {

	@InjectMocks
    private InteractionService interactionService = new InteractionServiceImpl();

	@Mock
	private MainNamesService mainNamesService;

	@Mock
	private InteractionDAO interactionDAO;

	@Mock
	private IsoformService isoService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
		mockMainNamesService(mainNamesService);
		mockInteractionDAO(interactionDAO);
	}

    @Test(expected = NextProtException.class)
    public void missingInteractantEntryAccessionShouldThrowException() {

	    interactionService.findInteractionsAsAnnotationsByEntry("NX_P49407");
    }

	private static void mockMainNamesService(MainNamesService mock) {

		Mockito.when(mock.findIsoformOrEntryMainName(any(String.class))).thenReturn(Optional.of(new MainNames()));
	}

	private static void mockInteractionDAO(InteractionDAO mock) {

		Interactant interactant = new Interactant();
		interactant.setAccession("P04435");
		interactant.setNextprot(true);
		interactant.setDatabase("UniProt");
		interactant.setXrefId(2688298L);

		Interaction interaction = new Interaction();
		interaction.setId(1L);
		interaction.setEvidenceId(1L);
		interaction.setEvidenceResourceId(1L);
		interaction.getInteractants().add(interactant);

		Mockito.when(mock.findInteractionsByEntry(any(String.class)))
				.thenReturn(Collections.singletonList(interaction));
	}
}