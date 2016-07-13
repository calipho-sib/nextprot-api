package org.nextprot.api.web.seo.service.impl;

import java.util.Set;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.web.seo.domain.SitemapUrl;
import org.nextprot.api.web.seo.domain.SitemapUrlSet;
import org.nextprot.api.web.seo.service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SitemapServiceImpl implements SitemapService {
	
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	
	
	@Override
	public SitemapUrlSet getSitemapUrlSet() {
		
		String base="https://search.nextprot.org";
		
		SitemapUrlSet urlSet = new SitemapUrlSet();

		// TODO: these are just examples, not validated
		urlSet.add(new SitemapUrl(base + "/about"));
		urlSet.add(new SitemapUrl(base + "/copyright"));
		urlSet.add(new SitemapUrl(base + "/news"));
		urlSet.add(new SitemapUrl(base + "/help"));
		urlSet.add(new SitemapUrl(base + "/help/simple-search"));
		
		Set<String> acs = masterIdentifierService.findUniqueNames();
		for (String ac: acs) {
			urlSet.add(new SitemapUrl(base + "/entry/" + ac + "/function"));
		}
		return urlSet;
	}
}
