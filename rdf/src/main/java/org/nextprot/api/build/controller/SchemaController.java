package org.nextprot.api.build.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.rdf.service.SchemaService;
import org.nextprot.api.rdf.service.SparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Lazy
@Controller
@Api(name = "Schema", description = "Method to retrieve schemas", group="Build rdf")
public class SchemaController {

	@Autowired private SchemaService schemaService;
	@Autowired private SparqlService advancedQueryService;
	@Autowired private ReleaseInfoService releaseInfoService;

	@ApiMethod(path = "/rdf/schema/protein", verb = ApiVerb.GET, description = "RDF schema for neXtProt ", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/protein")
	public String protein(Model model) {
		return "schema-entry-isoform";
	}	

	@ApiMethod(path = "/rdf/schema/source", verb = ApiVerb.GET, description = "RDF schema for neXtProt ", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/source")
	public String datasource(Model model) {
		model.addAttribute("datasourceList", this.schemaService.findAllSource());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-datasource-list";
	}	
	
	@ApiMethod(path = "/rdf/schema/database", verb = ApiVerb.GET, description = "RDF schema for neXtProt ", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/database")
	public String database(Model model) {
		model.addAttribute("databaseList", this.schemaService.findAllDatabase());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-database-list";
	}	
	
	@ApiMethod(path = "/rdf/schema/provenance", verb = ApiVerb.GET, description = "RDF schema for neXtProt ", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/provenance")
	public String provenances(Model model) {
		model.addAttribute("databaseList", this.schemaService.findAllProvenance());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-database-list";
	}	
	
	@ApiMethod(path = "/rdf/schema/evidence", verb = ApiVerb.GET, description = "RDF schema for neXtProt Evidence", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/evidence")
	public String evidence(Model model) {
		model.addAttribute("evidenceList", this.schemaService.findAllEvidence());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-evidence-list";
	}	
	
	@ApiMethod(path = "/rdf/schema/ontology", verb = ApiVerb.GET, description = "RDF schema for neXtProt ontology", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/ontology")
	public String findAllOntology(Model model) {
		model.addAttribute("ontologyList", this.schemaService.findAllOntology());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-ontology-list";
	}
		
	@ApiMethod(path = "/rdf/schema/annotation", verb = ApiVerb.GET, description = "RDF schema for neXtProt annotation", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema/annotation")
	public String findAllAnnotation(Model model) {
		model.addAttribute("annotationList", this.schemaService.findAllAnnotation());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-annotation-list";
	}

	@ApiMethod(path = "/rdf/schema", verb = ApiVerb.GET, description = "RDF schema for neXtProt ", produces = { "text/turtle"})
	@RequestMapping("/rdf/schema")
	public String all(Model model) {
		model.addAttribute("ontologyList", this.schemaService.findAllOntology());
		model.addAttribute("datasourceList", this.schemaService.findAllSource());
		model.addAttribute("databaseList", this.schemaService.findAllProvenance());
		model.addAttribute("annotationList", this.schemaService.findAllAnnotation());
		model.addAttribute("evidenceList", this.schemaService.findAllEvidence());
		model.addAttribute("version", this.schemaService.getTemplateVersion());
		model.addAttribute("releaseInfoVersions", this.releaseInfoService.findReleaseVersions());
		model.addAttribute("StringUtils", StringUtils.class);
		return "schema-all";
	}	

	

}

