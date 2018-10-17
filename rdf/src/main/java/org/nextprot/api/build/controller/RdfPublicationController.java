package org.nextprot.api.build.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

@Lazy
@Controller
@Api(name = "Publication", description = "Method to retrieve publications", group="Build rdf")
public class RdfPublicationController {

    @Autowired
    private GlobalPublicationService globalPublicationService;

	@Autowired
    private PublicationService publicationService;

	@Autowired
    private ViewResolver viewResolver;

	@ApiMethod(path = "/rdf/publication/{md5}", verb = ApiVerb.GET, description = "Exports one neXtProt publication.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/publication/{md5}")
	public String findOnePublicationByMd5(
            @ApiPathParam(name = "md5", description = "The md5 of the publication",  allowedvalues = { "b240aea6411ebd3cc49099009359df1f"})
            @PathVariable("md5") String md5, Model model) {

        Publication publication = publicationService.findPublicationByMD5(md5);

        model.addAttribute("publication", publication);
        model.addAttribute("prefix", true);
        model.addAttribute("StringUtils", StringUtils.class);
        model.addAttribute("isLargeScale", publicationService.getPublicationStatistics(publication.getPublicationId()).isLargeScale());

        return "publication";
	}
	
	//@ApiMethod(path = "/rdf/publication", verb = ApiVerb.GET, description = "Exports the whole neXtProt publication ordered by year and title.", produces = { "text/turtle"})
	@RequestMapping("/rdf/publication")
	public void findAllPublication(Map<String,Object> model, HttpServletResponse response, HttpServletRequest request) throws Exception {
		Boolean withPrefix=true;
		View v = viewResolver.resolveViewName("publication", Locale.ENGLISH);
		for(long pubId : globalPublicationService.findAllPublicationIds()){
			Publication publication = this.publicationService.findPublicationById(pubId);
			model.put("prefix", withPrefix);
			model.put("StringUtils", StringUtils.class);
			model.put("publication", publication);
	        model.put("isLargeScale", publicationService.getPublicationStatistics(publication.getPublicationId()).isLargeScale());
			v.render(model, request, response);
			withPrefix=false;
		}
	}
}

