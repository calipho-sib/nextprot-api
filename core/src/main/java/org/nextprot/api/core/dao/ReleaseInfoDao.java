package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;

public interface ReleaseInfoDao {

	List<ReleaseContentsDataSource> findReleaseInfoDataSources();
	String findDatabaseRelease();
	List<ReleaseStatsTag> findTagStatistics();

}
