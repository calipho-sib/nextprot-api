package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReleaseInfoServiceImpl implements ReleaseInfoService {

	@Autowired ReleaseInfoDao releaseInfoDao;
	
	@Override
	public ReleaseInfo findReleaseInfo() {
		ReleaseInfo ri = new ReleaseInfo();
		ri.setDatabaseRelease(releaseInfoDao.findNextProtRelease());
		ri.setDatasources(releaseInfoDao.findReleaseInfoDataSources());
		return ri;
	}


}
