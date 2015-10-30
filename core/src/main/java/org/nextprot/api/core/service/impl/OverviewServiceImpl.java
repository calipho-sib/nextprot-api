package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.EntityNameDao;
import org.nextprot.api.core.dao.HistoryDao;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformEntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityNameClass;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.service.FamilyService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Service
class OverviewServiceImpl implements OverviewService {

	@Autowired private HistoryDao historyDao;
	@Autowired private EntityNameDao entryNameDao;
	@Autowired private FamilyService familyService;
	@Autowired private IsoformService isoformService;
	

	@Override
	@Cacheable("overview")
	public Overview findOverviewByEntry(String uniqueName) {
		Overview overview = new Overview();
		List<History> history = this.historyDao.findHistoryByEntry(uniqueName);

		if (history != null && history.size() != 0)
			overview.setHistory(history.get(0));

		List<EntityName> entityNames = this.entryNameDao.findNames(uniqueName);
		entityNames.addAll(entryNameDao.findAlternativeChainNames(uniqueName));
		
		setNamesInOverview(entityNames, overview);

		overview.setFamilies(this.familyService.findFamilies(uniqueName));
		overview.setIsoformNames(convertIsoNamestoOverviewName(isoformService.findIsoformsByEntryName(uniqueName)));
		
		return overview;
	}
	
	private static List<EntityName> convertIsoNamestoOverviewName(List<Isoform> isoforms){
		
		List<EntityName> isoNames = new ArrayList<EntityName>();
		for(Isoform isoform : isoforms){
			
			EntityName name = new EntityName();
			name.setMain(true);
			name.setName(isoform.getMainEntityName().getValue());
			name.setType(isoform.getMainEntityName().getType());
			name.setQualifier(isoform.getMainEntityName().getQualifier());
			name.setSynonyms(new ArrayList<EntityName>());

			for(IsoformEntityName syn : isoform.getSynonyms()){

				EntityName s = new EntityName();
				s.setMain(false);
				s.setName(syn.getValue());
				name.setType(syn.getType());
				name.setQualifier(syn.getQualifier());
				
				name.getSynonyms().add(s);
			}
			
			isoNames.add(name);

		}
		
		return isoNames;

	}
	
	private void setNamesInOverview(List<EntityName> entityNames, Overview overview){

		Map<String, EntityName> entityMap = Maps.uniqueIndex(entityNames, new Function<EntityName, String>() {
			@Override
			public String apply(EntityName entityName) {
				return entityName.getId();
			}
		});

		Map<String, EntityName> mutableEntityMap = Maps.newHashMap(entityMap);
		String parentId = null;

		for (EntityName entityName : entityMap.values()) {

			parentId = entityName.getParentId();
			if (parentId != null && mutableEntityMap.containsKey(parentId)) {
				mutableEntityMap.get(parentId).addSynonym(entityName);
			} else {
				mutableEntityMap.put(entityName.getId(), entityName);
			}
		}

		List<EntityName> mutableEntityNames = new ArrayList<EntityName>(mutableEntityMap.values());

		for (EntityName entityName : mutableEntityMap.values())
			if (entityName.getParentId() != null)
				mutableEntityNames.remove(entityName);

		Multimap<Overview.EntityNameClass, EntityName> entryNameMap = Multimaps.index(mutableEntityNames, new Function<EntityName, EntityNameClass>() {
			@Override
			public EntityNameClass apply(EntityName entryName) {
				return entryName.getClazz();
			}
		});
		
		
		for (EntityNameClass en : entryNameMap.keySet()) {

			switch (en) {
			case PROTEIN_NAMES: {
				overview.setProteinNames(getSortedList(entryNameMap, en));
				break;
			}
			case GENE_NAMES: {
				overview.setGeneNames(getSortedList(entryNameMap, en));
				break;
			}
			case CLEAVED_REGION_NAMES: {
				overview.setCleavedRegionNames(getSortedList(entryNameMap, en));
				break;
			}
			case ADDITIONAL_NAMES: {
				overview.setAdditionalNames(getSortedList(entryNameMap, en));
				break;
			}
			case FUNCTIONAL_REGION_NAMES: {
				overview.setFunctionalRegionNames(getSortedList(entryNameMap, en));
				break;
			}
			}
		}
		
	
	}
	
	private static List<EntityName> getSortedList(Multimap<Overview.EntityNameClass, EntityName> entryMap, EntityNameClass en){
		List<EntityName> list = new ArrayList<EntityName>(entryMap.get(en));
		for(EntityName e : list){
			if(e.getSynonyms() != null){
				Collections.sort(e.getSynonyms());
			}
		}
		Collections.sort(list);
		return list;
	}

}
