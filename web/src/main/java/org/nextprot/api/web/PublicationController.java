package org.nextprot.api.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.ApiMethod;
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
public class PublicationController {

	@Autowired private PublicationService publicationService;

	//TODO remove comment below
	@Autowired 
	private ViewResolver viewResolver;
	
	@ApiMethod(path = "/rdf/publication/{md5}", verb = ApiVerb.GET, description = "Exports one neXtProt publication.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/publication/{md5}")
	public String findOnePublicationByMd5(
			@PathVariable("md5") String md5, Model model) {
		model.addAttribute("publication", this.publicationService.findPublicationByMD5(md5));
		model.addAttribute("prefix", true);
		model.addAttribute("StringUtils", StringUtils.class);
		return "publication";
	}

	@ApiMethod(path = "/rdf/publication/title/{title}", verb = ApiVerb.GET, description = "Exports one neXtProt publication.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/publication/title/{title}")
	public String findOnePublicationByTitle(
			@PathVariable("title") String title, Model model) {
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

