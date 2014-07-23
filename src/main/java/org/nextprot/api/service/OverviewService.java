package org.nextprot.api.service;

import org.nextprot.api.domain.Overview;
import org.nextprot.api.service.annotation.ValidEntry;

public interface OverviewService {
	Overview findOverviewByEntry(@ValidEntry String uniqueName);
}
