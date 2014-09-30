package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Keyword;

public interface KeywordDao {

	List<Keyword> findKeywordByMaster(String uniqueName);
}
