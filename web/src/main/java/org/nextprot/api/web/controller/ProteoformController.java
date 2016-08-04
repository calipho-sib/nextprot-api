package org.nextprot.api.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nextprot.api.annotation.builder.statement.service.StatementService;

@Lazy
@Controller
@Api(name = "EntryModified", description = "For example: may include an entry with one or several variants.")
public class ProteoformController {

	@Autowired	private EntryBuilderService entryBuilderService;
	@Autowired	private StatementService rawStatementService;
	
	@RequestMapping("/entry/{entryname}/proteoform")
	@ResponseBody
	public Map<String, Object> getSubPart(@PathVariable("entryname") String entryName, 
							 @ApiQueryParam(name = "goldOnly", required = false) Boolean goldOnly) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withTargetIsoforms());
		
		Map<String, Object> response = new HashMap<>();
		
		List<IsoformAnnotation> isoformAnnotations = rawStatementService.getNormalIsoformAnnotations(entryName);
		List<IsoformAnnotation> proteoformAnnotations = rawStatementService.getProteoformIsoformAnnotations(entryName);

		response.put("overview", entry.getOverview());
		response.put("isoforms", entry.getIsoforms());
		response.put("annotationsByIsoformAndCategory", isoformAnnotations.stream()
				.filter(ia -> (ia.getSubjectComponents() == null || ia.getSubjectComponents().isEmpty()))
				.collect(Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
								IsoformAnnotation::getApiTypeName,  TreeMap::new, Collectors.toList()))));

		
		response.put("proteoformAnnotations",  proteoformAnnotations.stream()
				.filter(ia -> (ia.getSubjectComponents() != null && !ia.getSubjectComponents().isEmpty())).
				collect( 
						Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
								IsoformAnnotation::getApiTypeName,  TreeMap::new, Collectors.toList()))));

		return response;
	}
	
}

