package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.aop.annotation.ValidEntry;
import org.nextprot.api.domain.Interaction;

public interface InteractionService {

	List<Interaction> findInteractionsByEntry(@ValidEntry String entryName);
	List<Interaction> findAllInteractions();
}
