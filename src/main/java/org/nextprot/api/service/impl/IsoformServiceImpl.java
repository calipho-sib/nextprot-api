package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.IsoformDAO;
import org.nextprot.api.domain.Isoform;
import org.nextprot.api.domain.IsoformEntityName;
import org.nextprot.api.service.IsoformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Lazy
@Service
public class IsoformServiceImpl implements IsoformService {

	@Autowired
	private IsoformDAO isoformDAO;


	@Override
	@Cacheable("isoforms")
	public List<Isoform> findIsoformsByEntryName(String entryName) {
		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		List<IsoformEntityName> synonyms = isoformDAO.findIsoformsSynonymsByEntryName(entryName);
		
		//Groups the synonyms by their main isoform
		Multimap<String, IsoformEntityName> synonymsMultiMap = Multimaps.index(synonyms, new SynonymFunction());
		for (Isoform isoform : isoforms) {
			isoform.setSynonyms(synonymsMultiMap.get(isoform.getUniqueName()));
		}
		
		return isoforms;
	}
	
	private class SynonymFunction implements Function<IsoformEntityName, String> {
		public String apply(IsoformEntityName isoformSynonym) {
			return isoformSynonym.getMainEntityName();
		}
	}

}
