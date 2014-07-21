package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.KeywordDao;
import org.nextprot.api.domain.Keyword;
import org.nextprot.api.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class KeywordServiceImpl implements KeywordService {

	@Autowired private KeywordDao keywordDao;
	
	@Override
	@Cacheable("keywords")
	public List<Keyword> findKeywordByMaster(String uniqueName) {
		return this.keywordDao.findKeywordByMaster(uniqueName);
	}

}
