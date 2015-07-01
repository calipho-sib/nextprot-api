package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.release.ReleaseInfoDataSource;

public interface ReleaseInfoDao {

	List<ReleaseInfoDataSource> findReleaseInfoDataSources();
	String findNextProtRelease();

}
