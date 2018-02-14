package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.core.domain.release.ReleaseStats;

public interface ReleaseInfoService {

	ReleaseInfo findReleaseInfo();
	ReleaseStats findReleaseStats();
}
