package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.KeywordDao;
import org.nextprot.api.core.domain.Keyword;
import org.nextprot.api.core.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class KeywordServiceImpl implements KeywordService {

	@Autowired private KeywordDao keywordDao;
	
	@Override
	@Cacheable("keywords")
	public List<Keyword> findKeywordByMaster(String uniqueName) {
		return this.keywordDao.findKeywordByMaster(uniqueName);
	}

}
