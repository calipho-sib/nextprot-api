package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.Identifier;
import org.nextprot.api.service.aop.ValidEntry;

public interface IdentifierService {

	List<Identifier> findIdentifiersByMaster(@ValidEntry String uniqueName);
}
