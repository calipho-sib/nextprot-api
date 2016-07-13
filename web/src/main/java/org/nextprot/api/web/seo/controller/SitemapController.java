package org.nextprot.api.web.seo.controller;

import org.nextprot.api.web.seo.domain.SitemapUrlSet;
import org.nextprot.api.web.seo.service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Lazy
@Controller
public class SitemapController {

	@Autowired
	private SitemapService sitemapService;

	@RequestMapping(value = "/sitemap", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public SitemapUrlSet sitemap() {

		return sitemapService.getSitemapUrlSet();

	}
}