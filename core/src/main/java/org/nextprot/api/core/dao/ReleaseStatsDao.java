package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;

import java.util.List;

public interface ReleaseStatsDao {

	List<ReleaseContentsDataSource> findReleaseInfoDataSources();
	List<ReleaseStatsTag> findTagStatistics();

}
