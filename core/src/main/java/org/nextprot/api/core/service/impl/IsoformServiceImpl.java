package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Service
class IsoformServiceImpl implements IsoformService {

	@Autowired
	private IsoformDAO isoformDAO;


	@Override
	@Cacheable("isoforms")
	public List<Isoform> findIsoformsByEntryName(String entryName) {
		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		List<EntityName> synonyms = isoformDAO.findIsoformsSynonymsByEntryName(entryName);
		
		//Groups the synonyms by their main isoform
		Multimap<String, EntityName> synonymsMultiMap = Multimaps.index(synonyms, new SynonymFunction());
		for (Isoform isoform : isoforms) {
			isoform.setSynonyms(synonymsMultiMap.get(isoform.getUniqueName()));
		}
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Isoform>().addAll(isoforms).build();
	}
	
	private class SynonymFunction implements Function<EntityName, String> {
		public String apply(EntityName isoformSynonym) {
			return isoformSynonym.getMainEntityName();
		}
	}

}
