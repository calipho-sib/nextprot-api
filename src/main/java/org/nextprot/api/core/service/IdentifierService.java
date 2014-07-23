package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface IdentifierService {

	List<Identifier> findIdentifiersByMaster(@ValidEntry String uniqueName);
}
