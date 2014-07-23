package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface OverviewService {
	Overview findOverviewByEntry(@ValidEntry String uniqueName);
}
