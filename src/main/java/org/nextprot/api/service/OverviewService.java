package org.nextprot.api.service;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.Overview;

public interface OverviewService {
	Overview findOverviewByEntry(@ValidEntry String uniqueName);
}
