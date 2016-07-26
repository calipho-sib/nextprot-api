package org.nextprot.api.web.seo.controller;

import javax.servlet.http.HttpServletRequest;

import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.service.SeoTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Lazy
@Controller
public class SeoController {

	@Autowired	private SeoTagsService seoTagsService;

	@RequestMapping(value = {"/seo/tags/**"}, method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public SeoTags getSeoTags(HttpServletRequest request) {
		String fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE ).toString();
		String url = fullUrl.substring("/seo/tags".length());
		return seoTagsService.getSeoTags(url);
	}

}

