package org.nextprot.api.core.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

@Lazy
@Controller
@Api(name = "Publication", description = "Method to retrieve publication")
public class PublicationController {


	@Autowired private PublicationService publicationService;

	//TODO remove comment below
	@Autowired 
	private ViewResolver viewResolver;
	
	@ApiMethod(path = "/rdf/publication/{md5}", verb = ApiVerb.GET, description = "Exports one neXtProt publication.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/publication/{md5}")
	public String findOnePublicationByMd5(
			@ApiParam(name = "md5", description = "The accession of the neXtProt publication. For example, 178d0394ef78390b0087c12f6c45686a", allowedvalues = { "178d0394ef78390b0087c12f6c45686a"}) @PathVariable("md5") String md5, Model model) {
		model.addAttribute("publication", this.publicationService.findPublicationByMD5(md5));
		model.addAttribute("prefix", true);
		model.addAttribute("StringUtils", StringUtils.class);
		return "publication";
	}

	@ApiMethod(path = "/rdf/publication/title/{title}", verb = ApiVerb.GET, description = "Exports one neXtProt publication.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/publication/title/{title}")
	public String findOnePublicationByTitle(
			@ApiParam(name = "title", description = "Part of the title of the neXtProt publication. For example, 'databasesA novel gene expressed in human adrenal gland'", allowedvalues = { "databasesA novel gene expressed in human adrenal gland"}) @PathVariable("title") String title, Model model) {
		model.addAttribute("publication", this.publicationService.findPublicationByTitle(title));
		model.addAttribute("prefix", true);
		model.addAttribute("StringUtils", StringUtils.class);
		return "publication";
	}	

	@ApiMethod(path = "/rdf/publication", verb = ApiVerb.GET, description = "Exports the whole neXtProt publication ordered by year and title.", produces = { "text/turtle"})
	@RequestMapping("/rdf/publication")
	public void findAllPublication(Map<String,Object> model, HttpServletResponse response, HttpServletRequest request) throws Exception {
		Boolean withPrefix=true;
		View v = viewResolver.resolveViewName("publication", Locale.ENGLISH);
		List<Long> publicationIds = this.publicationService.findAllPublicationIds();
		for(Long pubId :publicationIds){
			model.put("prefix", withPrefix);
			model.put("StringUtils", StringUtils.class);
			model.put("publication", this.publicationService.findPublicationById(pubId));
			model.put("StringUtils", StringUtils.class);
			v.render(model, request, response);
			withPrefix=false;
		}
		
//		
		
//		new ModelAndView("publication-list", model).compile().toString();
//		response.getOutputStream().write(bytes);
//		return "publication-list";
	}
	
}

