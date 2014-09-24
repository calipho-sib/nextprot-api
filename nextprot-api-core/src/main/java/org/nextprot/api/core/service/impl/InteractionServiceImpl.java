package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.InteractionDAO;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class InteractionServiceImpl implements InteractionService {

	@Autowired
	private InteractionDAO interactionDAO;

	@Override
	@Cacheable("interactions")
	public List<Interaction> findInteractionsByEntry(String entryName) {
		return interactionDAO.findInteractionsByEntry(entryName);
	}

	@Override
	public List<Interaction> findAllInteractions() {
		return interactionDAO.findAllInteractions();
	}

}
