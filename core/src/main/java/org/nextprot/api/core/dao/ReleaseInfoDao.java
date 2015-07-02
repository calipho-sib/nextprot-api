package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;

public interface ReleaseInfoDao {

	List<ReleaseContentsDataSource> findReleaseInfoDataSources();
	String findNextProtRelease();

}
