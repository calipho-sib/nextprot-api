package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Keyword;

public interface KeywordDao {

	List<Keyword> findKeywordByMaster(String uniqueName);
}
