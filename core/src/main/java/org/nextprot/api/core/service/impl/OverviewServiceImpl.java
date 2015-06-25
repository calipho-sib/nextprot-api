package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.dao.BioPhyChemPropsDao;
import org.nextprot.api.core.dao.EntityNameDao;
import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.dao.HistoryDao;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.core.domain.Overview.EntityNameClass;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Lazy
@Service
public class OverviewServiceImpl implements OverviewService {

	@Autowired private HistoryDao historyDao;
	@Autowired private EntityNameDao entryNameDao;
	@Autowired private FamilyDao familyDao;
	@Autowired private BioPhyChemPropsDao bioPhyChemPropsDao;

	@Override
	@Cacheable("overview")
	public Overview findOverviewByEntry(String uniqueName) {
		Overview overview = new Overview();
		List<History> history = this.historyDao.findHistoryByEntry(uniqueName);

		if (history != null && history.size() != 0)
			overview.setHistory(history.get(0));

		List<Overview.EntityName> entityNames = this.entryNameDao.findNames(uniqueName);
		
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

		List<EntityName> mutableEntityNames = new ArrayList<Overview.EntityName>(mutableEntityMap.values());

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
				overview.setProteinNames(new ArrayList<EntityName>(entryNameMap.get(en)));
				break;
			}
			case GENE_NAMES: {
				overview.setGeneNames(new ArrayList<EntityName>(entryNameMap.get(en)));
				break;
			}
			case CLEAVED_REGION_NAMES: {
				overview.setCleavedRegionNames(new ArrayList<EntityName>(entryNameMap.get(en)));
				break;
			}
			case ADDITIONAL_NAMES: {
				overview.setAdditionalNames(new ArrayList<EntityName>(entryNameMap.get(en)));
				break;
			}
			case FUNCTIONAL_REGION_NAMES: {
				overview.setFunctionalRegionNames(new ArrayList<EntityName>(entryNameMap.get(en)));
				break;
			}
			}
		}

		overview.setFamilies(this.familyDao.findFamilies(uniqueName));

		List<Pair<String, String>> props = this.bioPhyChemPropsDao.findPropertiesByUniqueName(uniqueName);

		List<Overview.BioPhysicalChemicalProperty> bpcp = new ArrayList<Overview.BioPhysicalChemicalProperty>();
		for(Pair<String, String> p :  props){
			bpcp.add(new Overview.BioPhysicalChemicalProperty(p.getFirst(), p.getSecond()));
		}
		overview.setBioPhyChemProps(bpcp);
		return overview;
	}

}
