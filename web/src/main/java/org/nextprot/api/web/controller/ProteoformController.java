package org.nextprot.api.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.nextprot.api.commons.utils.StringUtils;
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
	/*
	@Autowired	private EntryBuilderService entryBuilderService;
	@Autowired	private StatementService rawStatementService;
	
	@RequestMapping("/entry/{entryname}/proteoform")
	@ResponseBody
	public Map<String, Object> getSubPart(@PathVariable("entryname") String isoformAccession, 
							 @ApiQueryParam(name = "goldOnly", required = false) Boolean goldOnly) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(isoformAccession).withOverview().withTargetIsoforms());
		
		Map<String, Object> wrapperResponse =  new HashMap<>();
		
		Map<String, Object> response = new HashMap<>();
		
		List<IsoformAnnotation> isoformAnnotations = rawStatementService.getIsoformAnnotations(isoformAccession);

		response.put("overview", entry.getOverview());
		response.put("isoforms", entry.getIsoforms());
		response.put("annotationsByIsoformAndCategory", isoformAnnotations.stream()
				.filter(ia -> (!ia.isProteoformAnnotation()))
				.collect(Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(i -> {
							return StringUtils.camelToKebabCase(i.getApiTypeName());
						},  TreeMap::new, Collectors.toList()))));

		
		response.put("proteoformAnnotations",  isoformAnnotations.stream()
				.filter(ia -> (ia.isProteoformAnnotation())).
				collect( 
						Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(i -> {
							return StringUtils.camelToKebabCase(i.getApiTypeName());
						},  TreeMap::new, Collectors.toList()))));

		wrapperResponse.put("entry", response);
		return wrapperResponse;
	}*/
	
}

