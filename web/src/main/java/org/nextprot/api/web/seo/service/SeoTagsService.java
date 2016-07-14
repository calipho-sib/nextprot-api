package org.nextprot.api.web.seo.service;

import java.util.List;

import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.domain.SeoTagsAndUrl;

public interface SeoTagsService {
	
	SeoTags getSeoTags(String url);

	List<SeoTagsAndUrl> getHardCodedSeoTags();


}
