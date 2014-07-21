package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.Keyword;

public interface KeywordService {

	List<Keyword> findKeywordByMaster(@ValidEntry String uniqueName);
}