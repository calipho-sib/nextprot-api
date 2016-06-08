package org.nextprot.api.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nextprot.api.annotation.builder.statement.service.RawStatementService;

@Lazy
@Controller
@Api(name = "EntryModified", description = "For example: may include an entry with one or several variants.")
public class EntryModifiedController {

	@Autowired	private EntryBuilderService entryBuilderService;
	@Autowired	private RawStatementService rawStatementService;
	
	@RequestMapping("/entry/{entryname}/modified-entry-annotation")
	public String getSubPart(@PathVariable("entryname") String entryName, @ApiQueryParam(name = "pub", required = false) Boolean pub, @ApiQueryParam(name = "xref", required = false) Boolean xref, @ApiQueryParam(name = "xp", required = false) Boolean xp,  Model model) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withTargetIsoforms());

		Map<String, List<IsoformAnnotation>> annotationsByIsoforms = new HashMap<>();
		annotationsByIsoforms.put(entryName + "-1", rawStatementService.getNormalAnnotations(entryName));
		entry.setModifiedEntryAnnotations(rawStatementService.getModifiedEntryAnnotation(entryName));
		
		model.addAttribute("entry", entry);

		if(pub == null || !pub){ entry.setPublications(null);}
		if(xp == null || !xp){ entry.setExperimentalContexts(null); }
		if(xref == null || !xref){ entry.setXrefs(null); }

		return "entry";
	}
}

