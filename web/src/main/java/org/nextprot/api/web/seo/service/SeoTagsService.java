package org.nextprot.api.web.seo.service;

import java.util.List;

import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.domain.SeoTagsAndUrl;

public interface SeoTagsService {
	
	// dedicated SEO tags services
	SeoTags getGitHubSeoTags(String url);
	SeoTags getDefaultSeoTags(String url);
	SeoTags getNewsSeoTags(String url);
	SeoTags getPublicationSeoTags(String url);
	SeoTags getTermSeoTags(String url);
	SeoTags getEntrySeoTags(String url);
	
	// used for building Sitemap
	List<SeoTagsAndUrl> getGitHubSeoTags();
	
}
