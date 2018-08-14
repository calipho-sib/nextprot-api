package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.service.EntityNameService;
import org.springframework.stereotype.Service;


@Service
class EntityNameServiceImpl implements EntityNameService {

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
}
