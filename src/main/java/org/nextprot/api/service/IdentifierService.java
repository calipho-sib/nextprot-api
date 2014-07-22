package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.aop.annotation.ValidEntry;
import org.nextprot.api.domain.Identifier;

public interface IdentifierService {

	List<Identifier> findIdentifiersByMaster(@ValidEntry String uniqueName);
}
