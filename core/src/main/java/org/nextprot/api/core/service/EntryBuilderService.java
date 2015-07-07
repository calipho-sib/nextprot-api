package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.EntryConfig;

public interface EntryBuilderService {

	Entry build(EntryConfig fluentEntry);

}
