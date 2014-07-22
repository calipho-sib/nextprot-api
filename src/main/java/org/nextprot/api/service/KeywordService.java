package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.Keyword;
import org.nextprot.api.service.aop.ValidEntry;

public interface KeywordService {

	List<Keyword> findKeywordByMaster(@ValidEntry String uniqueName);
}