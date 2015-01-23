package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Keyword;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface KeywordService {

	List<Keyword> findKeywordByMaster(@ValidEntry String uniqueName);
}