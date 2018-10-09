package org.nextprot.api.core.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.dao.EntityNameDao;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.EntityNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
class EntityNameServiceImpl implements EntityNameService {

    @Autowired
    private EntityNameDao entryNameDao;

	@Override
	public boolean hasNameIgnoreCase(EntityName entityName, String name) {

		if (entityName != null) {

			if (name.equalsIgnoreCase(entityName.getName())) {
				return true;
			}

			else if (name.equalsIgnoreCase(entityName.getMainEntityName())) {
				return true;
			}

			else if (name.equalsIgnoreCase(entityName.getId())) {
				return true;
			}

			else if (StreamUtils.nullableListToStream(entityName.getOtherRecommendedEntityNames())
					.anyMatch(rn -> name.equalsIgnoreCase(rn.getName()))) {
				return true;
			}

			else if (StreamUtils.nullableListToStream(entityName.getSynonyms())
					.anyMatch(s -> name.equalsIgnoreCase(s.getName()))) {
				return true;
			}
		}

		return false;
	}

    @Override
    public List<EntityName> findNamesByEntityNameClass(String uniqueName, Overview.EntityNameClass entityNameClass) {

        Map<Overview.EntityNameClass, List<EntityName>> map = findNamesByEntityNameClass(uniqueName);

        if (!map.containsKey(entityNameClass)) {

            return new ArrayList<>();
        }

	    return map.get(entityNameClass);
    }

    // TODO: refactor this not efficient code
    private Map<Overview.EntityNameClass, List<EntityName>> findNamesByEntityNameClass(String uniqueName) {

        List<EntityName> entityNames = entryNameDao.findNames(uniqueName);
        entityNames.addAll(entryNameDao.findAlternativeChainNames(uniqueName));

        Map<Overview.EntityNameClass, List<EntityName>> map = new EnumMap<>(Overview.EntityNameClass.class);

        Map<String, EntityName> entityMap = Maps.uniqueIndex(entityNames, EntityName::getId);

        Map<String, EntityName> mutableEntityMap = Maps.newHashMap(entityMap);
        String parentId;

        for (EntityName entityName : entityMap.values()) {

            parentId = entityName.getParentId();
            if (parentId != null && mutableEntityMap.containsKey(parentId)) {

                if (entityName.isMain()) {
                    mutableEntityMap.get(parentId).addOtherRecommendedEntityName(entityName);
                }
                else {
                    mutableEntityMap.get(parentId).addSynonym(entityName);
                }
            } else {
                mutableEntityMap.put(entityName.getId(), entityName);
            }
        }

        List<EntityName> mutableEntityNames = new ArrayList<>(mutableEntityMap.values());

        for (EntityName entityName : mutableEntityMap.values())
            if (entityName.getParentId() != null)
                mutableEntityNames.remove(entityName);

        Multimap<Overview.EntityNameClass, EntityName> entryNameMap = Multimaps.index(mutableEntityNames, EntityName::getClazz);

        for (Overview.EntityNameClass en : entryNameMap.keySet()) {

            switch (en) {
                case PROTEIN_NAMES: {
                    map.put(en, getSortedList(entryNameMap, en));
                    break;
                }
                case GENE_NAMES: {
                    map.put(en, getSortedList(entryNameMap, en, Comparator.comparing(EntityName::getName)));
                    break;
                }
                case CLEAVED_REGION_NAMES: {
                    map.put(en, getSortedList(entryNameMap, en));
                    break;
                }
                case ADDITIONAL_NAMES: {
                    map.put(en, getSortedList(entryNameMap, en));
                    break;
                }
                case FUNCTIONAL_REGION_NAMES: {
                    map.put(en, getSortedList(entryNameMap, en));
                    break;
                }
            }
        }

        return map;
    }

    private static List<EntityName> getSortedList(Multimap<Overview.EntityNameClass, EntityName> entryMap, Overview.EntityNameClass en) {

        return getSortedList(entryMap, en, EntityName.newDefaultComparator());
    }

    private static List<EntityName> getSortedList(Multimap<Overview.EntityNameClass, EntityName> entryMap, Overview.EntityNameClass en, Comparator<EntityName> comparator) {

        List<EntityName> list = new ArrayList<>(entryMap.get(en));
        for(EntityName e : list){
            if(e.getSynonyms() != null){
                e.getSynonyms().sort(comparator);
            }
        }
        list.sort(comparator);

        return list;
    }
}
