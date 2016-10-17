package org.nextprot.api.web.seo.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.RelativeUrlUtils;
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

	private final Log Logger = LogFactory.getLog(SeoController.class);

	@Autowired	private SeoTagsService seoTagsService;

	@RequestMapping(value = {"/seo/tags/**"}, method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public SeoTags getSeoTags(HttpServletRequest request) {

		try{
		
			String fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE ).toString();
			String url = fullUrl.substring("/seo/tags".length());
		
			SeoTags tags = seoTagsService.getGitHubSeoTags(url);
			if (tags!=null) return tags;
	
			String firstElement = RelativeUrlUtils.getPathElements(url)[0];
			
			if ("entry".equals(firstElement)) {
				return seoTagsService.getEntrySeoTags(url);
			}
			if ("term".equals(firstElement)) {
				return seoTagsService.getTermSeoTags(url);
			}
			if ("publication".equals(firstElement)) {
				return seoTagsService.getPublicationSeoTags(url);
			}
			if ("news".equals(firstElement)) {
				return seoTagsService.getNewsSeoTags(url);
			}
			// default behavior
			Logger.warn("No explicit SEO tags were found for this page: " + url );
			return seoTagsService.getDefaultSeoTags(url);
			
		} catch (Exception e) {
			throw new NextProtException(e);
		}
	
	}

}

