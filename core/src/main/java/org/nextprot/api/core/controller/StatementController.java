package org.nextprot.api.core.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.nextprot.api.core.service.StatementService;


@Lazy
@Controller
@Api(name = "Statement Annotations", description = "Annotations built from flat statements")
public class StatementController {

	@Autowired	private StatementService statementService;
	
	@RequestMapping("/statements/entry/{entryname}")
	@ResponseBody
	public List<Annotation> getSubPart(@PathVariable("entryname") String entryAccession) {
		return statementService.getAnnotations(entryAccession);
	}
	
}
