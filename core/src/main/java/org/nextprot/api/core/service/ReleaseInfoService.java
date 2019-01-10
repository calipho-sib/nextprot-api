package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.release.ReleaseInfoDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoStats;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;

public interface ReleaseInfoService {

	ReleaseInfoVersions findReleaseVersions();
	ReleaseInfoStats findReleaseStats();
	ReleaseInfoDataSources findReleaseDatasources();
	ReleaseStatsTag getAnnotationWithoutFunctionTag();
}
