package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
class IsoformServiceImpl implements IsoformService {

	@Autowired
	private IsoformDAO isoformDAO;

	
	@Autowired
	private MasterIsoformMappingDao masterIsoformMappingDAO;

	
	@Override
	@Cacheable("isoforms")
	public List<Isoform> findIsoformsByEntryName(String entryName) {
		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		List<EntityName> synonyms = isoformDAO.findIsoformsSynonymsByEntryName(entryName);
		Map<String,List<NucleotidePositionRange>> isoMasterNuPosRanges = masterIsoformMappingDAO.findMasterIsoformMapping(entryName);
				
		//Groups the synonyms by their main isoform
		Multimap<String, EntityName> synonymsMultiMap = Multimaps.index(synonyms, new SynonymFunction());
		for (Isoform isoform : isoforms) {
			isoform.setSynonyms(synonymsMultiMap.get(isoform.getUniqueName()));
		}

		//Attach master mapping to each isoform
		for (Isoform isoform : isoforms) {
			if (isoMasterNuPosRanges.containsKey(isoform.getUniqueName())) {
				isoform.setMasterMapping(isoMasterNuPosRanges.get(isoform.getUniqueName()));
			} else {
				isoform.setMasterMapping(new ArrayList<>());
			}
		}

		isoforms.sort((i1, i2) -> new IsoformUtils.IsoformComparator().compare(i1, i2));

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Isoform>().addAll(isoforms).build();
	}
	
	private class SynonymFunction implements Function<EntityName, String> {
		public String apply(EntityName isoformSynonym) {
			return isoformSynonym.getMainEntityName();
		}
	}

}
