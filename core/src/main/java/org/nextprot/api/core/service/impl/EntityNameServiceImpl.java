package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.service.EntityNameService;
import org.springframework.stereotype.Service;


@Service
class EntityNameServiceImpl implements EntityNameService {

	@Override
	public boolean hasName(EntityName entityName, String name) {

		if (entityName != null) {

			if (name.equals(entityName.getName())) {
				return true;
			}

			else if (name.equals(entityName.getMainEntityName())) {
				return true;
			}

			else if (name.equals(entityName.getId())) {
				return true;
			}

			else if (StreamUtils.nullableListToStream(entityName.getOtherRecommendedEntityNames())
					.anyMatch(rn -> name.equals(rn.getName()))) {
				return true;
			}

			else if (StreamUtils.nullableListToStream(entityName.getSynonyms())
					.anyMatch(s -> name.equals(s.getName()))) {
				return true;
			}
		}

		return false;
	}
}
