package org.nextprot.api.web.seo.service.impl;

import java.util.Set;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.web.domain.NextProtNews;
import org.nextprot.api.web.seo.domain.SeoTagsAndUrl;
import org.nextprot.api.web.seo.domain.SitemapUrl;
import org.nextprot.api.web.seo.domain.SitemapUrlSet;
import org.nextprot.api.web.seo.service.SeoTagsService;
import org.nextprot.api.web.seo.service.SitemapService;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SitemapServiceImpl implements SitemapService {
	
	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired 
	private GitHubService githubService;
	
	@Autowired 
	private SeoTagsService seoTagService;
	
	
	@Override
	public SitemapUrlSet getSitemapUrlSet() {
		
		//TODO set this elsewhere !
		String base="https://www.nextprot.org";
		
		SitemapUrlSet urlSet = new SitemapUrlSet();

		// news urls
		for (NextProtNews n: githubService.getNews()) {
			urlSet.add(new SitemapUrl(base + "/news/" + n.getUrl()));
		}

		// other application urls (help, about, home, portals, ...)
		for (SeoTagsAndUrl tag: seoTagService.getHardCodedSeoTags()) {
			urlSet.add(new SitemapUrl(base + tag.getUrl()));
		}
		
		// entry function urls
		Set<String> acs = masterIdentifierService.findUniqueNames();
		for (String ac: acs) {
			urlSet.add(new SitemapUrl(base + "/entry/" + ac + "/function"));
		}
		
		// TODO later
		// add other entry pages (interactions, sequence, proteomics,...
		// add term pages ? 
		// ad publi pages ?
		
		
		return urlSet;
	}
}
