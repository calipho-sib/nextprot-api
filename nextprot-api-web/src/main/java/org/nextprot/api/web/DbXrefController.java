package org.nextprot.api.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

@Lazy
@Controller
@Api(name = "DbXref", description = "Method to retrieve the list of existing cross references", role = "ROLE_RDF")
public class DbXrefController {

	
	@Autowired private DbXrefService xrService;

	@Autowired private ViewResolver viewResolver;

	@ApiMethod(path = "/rdf/xrefs/ids", verb = ApiVerb.GET, description = "Exports list of xrefs ids", produces = { "text/turtle"})
	@RequestMapping("/rdf/xrefs/ids")
	public String findAllXrefsIds(Model model) {
		model.addAttribute("xrefIds", this.xrService.getAllDbXrefsIds());
		model.addAttribute("StringUtils", StringUtils.class);
		return "xref-ids";
	}
	
	@ApiMethod(path = "/rdf/xrefs", verb = ApiVerb.GET, description = "Exports list of xrefs", produces = { "text/turtle"})
	@RequestMapping("/rdf/xrefs")
	public void findAllXrefs(Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
		List<Long> ids = this.xrService.getAllDbXrefsIds(); // too many data, memory errors... should stream rather than list...
		//for (Long id : ids) System.out.println("fulllist - id: " + id);
		int idx =0;
		int bunchSize = 100000;
		int bunchCount=0;

		NPFileFormat format = getRequestedFormat(request);
		String fileName = "nextprot-xrefs" + "." + format.getExtension() ;
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		View v = viewResolver.resolveViewName("prefix", Locale.ENGLISH);
		v.render(model.asMap(), request, response);

		while(true) {
			bunchCount++;
			int idx2= idx+bunchSize;
			if (idx2>ids.size()) idx2=ids.size();
			System.out.println("bunch: " + bunchCount + " - indices: " + idx + " - " + idx2 );
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			// do the job
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			List<Long> someIds=ids.subList(idx, idx2);
			List<DbXref> refs = this.xrService.findDbXRefByIds(someIds);
			model.addAttribute("bunch", bunchCount);
			model.addAttribute("xrefIds", refs);
			model.addAttribute("StringUtils", StringUtils.class);
			v = viewResolver.resolveViewName("xref-all", Locale.ENGLISH);
			v.render(model.asMap(), request, response);
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			idx = idx2;
			if (idx==ids.size()) break;
		}

	}

	
	
	private NPFileFormat getRequestedFormat(HttpServletRequest request) {
		NPFileFormat format = null;
		String uri = request.getRequestURI();
		if (uri.toLowerCase().endsWith(".ttl")) {
			format = NPFileFormat.TURTLE;
		} else if (uri.toLowerCase().endsWith(".xml")) {
			format = NPFileFormat.XML;
		} else if (uri.toLowerCase().endsWith(".json")) {
			format = NPFileFormat.JSON;
		} else if (uri.toLowerCase().endsWith(".txt")) {
			format = NPFileFormat.TXT;
		} else
			throw new NextProtException("Format not recognized");
		return format;
	}

	
}

