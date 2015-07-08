package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.dao.InteractionDAO;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.BinaryInteraction2Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class InteractionServiceImpl implements InteractionService {

	@Autowired private InteractionDAO interactionDAO;
	@Autowired private IsoformService isoService;

	@Override
	@Cacheable("interactions")
	public List<Interaction> findInteractionsByEntry(String entryName) {
		List<Interaction> interactions = interactionDAO.findInteractionsByEntry(entryName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Interaction>().addAll(interactions).build();

	}

	@Override
	@Cacheable("interactions-as-annot")
	public List<Annotation> findInteractionsAsAnnotationsByEntry(String entryName) {
		List<Annotation> annots = new ArrayList<Annotation>();
		List<Isoform> isoforms = this.isoService.findIsoformsByEntryName(entryName);
		List<Interaction> interactions = this.interactionDAO.findInteractionsByEntry(entryName);
		for (Interaction inter : interactions) {
			Annotation annot = BinaryInteraction2Annotation.transform(inter, entryName, isoforms);
			annots.add(annot);
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(annots).build();

	}

}
