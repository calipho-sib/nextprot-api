package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface InteractionService {

	List<Interaction> findInteractionsByEntry(@ValidEntry String entryName);
	List<Interaction> findAllInteractions();
}
