package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Lazy
@Controller
@Api(name = "EntryModified", description = "For example: may include an entry with one or several variants.")
public class EntryModifiedController {

	@Autowired	private EntryBuilderService entryBuilderService;

	@RequestMapping("/entry/{entryname}/modified-entry-annotation")
	public String getSubPart(@PathVariable("entryname") String entryName, @PathVariable("pub") Boolean pub, @PathVariable("xref") Boolean xref,  @PathVariable("xp") Boolean xp,  Model model) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withAnnotations().withModifiedEntryAnnotations().withOverview().withTargetIsoforms());
		model.addAttribute("entry", entry);
		if(pub == null || !pub){
			entry.setPublications(null);
		}
		if(xp == null || !xp){
			entry.setExperimentalContexts(null);
		}

		if(xref == null || !xref){
			entry.setXrefs(null);
		}

		return "entry";
	}
}

