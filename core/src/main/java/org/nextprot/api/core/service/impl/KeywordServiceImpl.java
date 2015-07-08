package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.KeywordDao;
import org.nextprot.api.core.domain.Keyword;
import org.nextprot.api.core.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class KeywordServiceImpl implements KeywordService {

	@Autowired private KeywordDao keywordDao;
	
	@Override
	@Cacheable("keywords")
	public List<Keyword> findKeywordByMaster(String uniqueName) {
		 List<Keyword> keywords = this.keywordDao.findKeywordByMaster(uniqueName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Keyword>().addAll(keywords).build();
	}

}
