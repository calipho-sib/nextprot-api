package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface FamilyService {

	List<Family> findFamilies(@ValidEntry String uniqueName);
}