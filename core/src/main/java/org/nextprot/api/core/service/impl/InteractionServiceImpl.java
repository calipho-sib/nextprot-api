package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.InteractionDAO;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.utils.BinaryInteraction2Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class InteractionServiceImpl implements InteractionService {

	@Autowired private InteractionDAO interactionDAO;
	@Autowired private IsoformService isoService;
	@Autowired private MainNamesService mainNamesService;

	@Override
	@Cacheable(value = "interactions", sync = true)
	public List<Interaction> findInteractionsByEntry(String entryName) {
		List<Interaction> interactions = interactionDAO.findInteractionsByEntry(entryName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Interaction>().addAll(interactions).build();

	}

	@Override
	@Cacheable(value = "interactions-as-annot", sync = true)
	public List<Annotation> findInteractionsAsAnnotationsByEntry(String entryName) {
		List<Annotation> annots = new ArrayList<>();
		List<Isoform> isoforms = this.isoService.findIsoformsByEntryName(entryName);

		for (Interaction interaction : this.interactionDAO.findInteractionsByEntry(entryName)) {

			try {
				annots.add(BinaryInteraction2Annotation.transform(interaction, entryName, isoforms, mainNamesService));
			} catch (BinaryInteraction2Annotation.MissingInteractantEntryException e) {

				String interactionString = interaction.getId() + " (db="+ interaction.getEvidenceDatasource()+", url="+interaction.getEvidenceXrefURL()+")";

				throw new NextProtException("Cannot create BioObject for interaction "+interactionString +" in entry "+entryName, e);
			}
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(annots).build();

	}

}
