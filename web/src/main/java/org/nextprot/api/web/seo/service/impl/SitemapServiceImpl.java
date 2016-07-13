package org.nextprot.api.web.seo.service.impl;

import java.util.Set;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.web.seo.domain.SitemapUrl;
import org.nextprot.api.web.seo.domain.SitemapUrlSet;
import org.nextprot.api.web.seo.service.SitemapService;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SitemapServiceImpl implements SitemapService {
	
	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired GitHubService githubService;
	
	@Override
	public SitemapUrlSet getSitemapUrlSet() {
		
		String base="https://search.nextprot.org";
		
		SitemapUrlSet urlSet = new SitemapUrlSet();

		// TODO: just an example, to be removed
		urlSet.add(new SitemapUrl(base + "/test/pam"));
		
		Set<String> acs = masterIdentifierService.findUniqueNames();
		for (String ac: acs) {
			urlSet.add(new SitemapUrl(base + "/entry/" + ac + "/function"));
		}
		return urlSet;
	}
}
