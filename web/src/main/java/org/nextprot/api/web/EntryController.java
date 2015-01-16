package org.nextprot.api.web;

import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiParamType;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.domain.Keyword;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.AuthorService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IdentifierService;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.KeywordService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Lazy
@Controller
@Api(name = "Entry", description = "Method to retrieve a complete or partial entry")
public class EntryController {

	@Autowired
	private FluentEntryService fluentEntryService;

	@Autowired private AntibodyMappingService antibodyService;
	@Autowired private EntryService entryService;
	@Autowired private IsoformService isoformService;
	@Autowired private GeneService geneService;
	@Autowired private AnnotationService annotationService;
	@Autowired private KeywordService keywordService;
	@Autowired private OverviewService overviewService;
	@Autowired private PeptideMappingService peptideService;
	@Autowired private GenomicMappingService genomicService;
	@Autowired private IdentifierService identifierService;
	@Autowired private PublicationService publicationService;
	@Autowired private AuthorService authorService;
	@Autowired private DbXrefService xrefService;
	@Autowired private InteractionService interactionService;
	@Autowired private ExperimentalContextService expContextService;

	@ApiMethod(path = "/entry/{entry}", verb = ApiVerb.GET, description = "Exports the whole neXtProt entry, this includes: The overview, the annotations, the keywords, the interactions, the isoforms, the chromosomal location, the genomic mapping, the list of identifiers, the publications, the cross references, the list of peptides, the list of the antibodies and the experimental contexts", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping(value = "/entry/{entry}", method = { RequestMethod.GET })
	public String exportEntry(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName, Model model) {
		List<Entry> proteinList = new ArrayList<Entry>();
		proteinList.add(this.entryService.findEntry(entryName));
		model.addAttribute("entryList", proteinList);
		model.addAttribute("StringUtils", StringUtils.class);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		return "exportEntries";
	}
	

	@RequestMapping("/entry/{entryname}/{subpart}")
	public String getSubPart(@PathVariable("entryname") String entryName, @PathVariable("subpart") String subpart, Model model) {
		
		Entry dummy = this.fluentEntryService.getNewEntry(entryName).withView(subpart);
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "annotation-list";
	}

	
	@ApiMethod(path = "/entry/{entry}/protein-sequence", verb = ApiVerb.GET, description = "Gets the isoforms for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/protein-sequence")
	public String getIsoforms(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry dummy = new Entry(entryName);
		dummy.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "protein-sequence-list";
	}
	
	@ApiMethod(path = "/entry/{entry}/keyword", verb = ApiVerb.GET, description = "Gets the list of keywords for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/keyword")
	public String getKeywords(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY, allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<Keyword> keywords = this.keywordService.findKeywordByMaster(entryName);
		Entry entry = new Entry(entryName);
		entry.setKeywords(keywords);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "keyword-list";
	}

	@ApiMethod(path = "/entry/{entry}/overview", verb = ApiVerb.GET, description = "Gets an overview of the entry. This includes the protein existence, protein names, gene names, functional region names, cleaved region names, the families, the bio physical and chemical properties and the history. See the Overview object for more details.", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/overview")
	public String getOverview(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = new Entry(entryName);
		entry.setOverview(overviewService.findOverviewByEntry(entryName));
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "overview";
	}
	
	@ApiMethod(path = "/entry/{entry}/antibody", verb = ApiVerb.GET, description = "Gets the list of antibodies for a given entry if any.", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/antibody")
	public String getAntibodyMapping(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry which contains antibodies. For example, insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<AntibodyMapping> mapping = this.antibodyService.findAntibodyMappingByUniqueName(entryName);
		Entry entry = new Entry(entryName);
		entry.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
		entry.setAntibodyMappings(mapping);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "antibody-list";
	}
	
	@ApiMethod(path = "/entry/{entry}/peptide", verb = ApiVerb.GET, description = "Gets the list of peptides for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/peptide")
	public String getPeptideMapping(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<PeptideMapping> mapping = this.peptideService.findNaturalPeptideMappingByMasterUniqueName(entryName);
		Entry entry = new Entry(entryName);
		entry.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
		entry.setPeptideMappings(mapping);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "peptide-list";
	}

	@ApiMethod(path = "/entry/{entry}/srm-peptide", verb = ApiVerb.GET, description = "Gets the list of SRM peptides for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/srm-peptide")
	public String getSrmPeptideMapping(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<PeptideMapping> mapping = this.peptideService.findSyntheticPeptideMappingByMasterUniqueName(entryName);
		Entry entry = new Entry(entryName);
		entry.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
		entry.setPeptideMappings(mapping);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "srm-peptide-list";
	}

	@ApiMethod(path = "/entry/{entry}/identifier", verb = ApiVerb.GET, description = "Gets the list of identifiers for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/identifier")
	public String getIdentifiers(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<Identifier> identifiers = this.identifierService.findIdentifiersByMaster(entryName);
		Entry entry = new Entry(entryName);
		entry.setIdentifiers(identifiers);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "identifier-list";
	}
	
	@RequestMapping("/entry/{entry}/genomic/chromosomal-location")
	@ApiMethod(path = "/entry/{entry}/genomic/chromosomal-location", verb = ApiVerb.GET, description = "Gets the chromosomal locations of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	public String getChromosomalLocation(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = new Entry(entryName);
		entry.setChromosomalLocations(geneService.findChromosomalLocationsByEntry(entryName));
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "chromosomal-location-list";
	}

	@ApiMethod(path = "/entry/{entry}/genomic/genomic-mapping", verb = ApiVerb.GET, description = "Gets the genomic mappings for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/genomic/genomic-mapping")
	public String getGenomicMapping(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry dummy = new Entry(entryName);
		dummy.setGenomicMappings(genomicService.findGenomicMappingsByEntryName(entryName));
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "genomic-mapping-list";
	}

	@ApiMethod(path = "/entry/{entry}/genomic", verb = ApiVerb.GET, description = "Gets the genomic mappings for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/genomic")
	public String getGenomic(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry dummy = new Entry(entryName);
		dummy.setGenomicMappings(genomicService.findGenomicMappingsByEntryName(entryName));
		dummy.setChromosomalLocations(geneService.findChromosomalLocationsByEntry(entryName));
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "genomic";
	}

	
	@ApiMethod(path = "/entry/{entry}/publication", verb = ApiVerb.GET, description = "Gets the publications of an given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/publication")
	public String publications(@ApiParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		List<Publication> publications = this.publicationService.findPublicationsByMasterUniqueName(entryName);
		Entry entry = new Entry(entryName);
		entry.setPublications(publications);
		model.addAttribute("entry", entry);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "publication-list";
	}
	
	@ApiMethod(path = "/entry/{entry}/xref", verb = ApiVerb.GET, description = "Gets the cross references of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/xref")
	public String getXrefs(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) 
			@PathVariable("entry") String entryName, Model model) {
		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster(entryName);
		Entry dummy = new Entry(entryName);
		dummy.setXrefs(xrefs);
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "xref-list";
	}


	@ApiMethod(path = "/entry/{entry}/interaction", verb = ApiVerb.GET, description = "Gets the interactions of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/interaction")
	public String interactions(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry for example: The cytoplasmic tyrosine-protein kinase BMX: NX_P51813", paramType=ApiParamType.QUERY, allowedvalues = { "NX_P51813"}) 
			@PathVariable("entry") String entryName, Model model) {
		Entry dummy = new Entry(entryName);
		dummy.setInteractions(interactionService.findInteractionsByEntry(entryName));
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "interaction-list";
	}

	@ApiMethod(path = "/entry/{entry}/annotation", verb = ApiVerb.GET, description = "Gets the annotations of a given entry grouped by category", produces = { MediaType.APPLICATION_XML_VALUE})
	@RequestMapping("/entry/{entry}/annotation")
	public String getEntryAnnotations(
			@ApiParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) 
			@PathVariable("entry") String entryName, Model model) {
		List<Annotation> annotations = this.annotationService.findAnnotations(entryName);
		Entry dummy = new Entry(entryName);
		dummy.setAnnotations(annotations);
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "annotation-list";
	}
	
	@ApiMethod(path = "/entry/{entry}/experimentalContext", verb = ApiVerb.GET, description = "Gets the experimental contexts related to the annotations of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/experimentalContext")
	public String getEntryExperimentalContexts(@ApiParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308", paramType=ApiParamType.QUERY,  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry dummy = new Entry(entryName);
		dummy.setExperimentalContexts(this.expContextService.findExperimentalContextsByEntryName(entryName));
		model.addAttribute("entry", dummy);
		model.addAttribute("NXUtils", new NXVelocityUtils());
		model.addAttribute("StringUtils", StringUtils.class);
		return "experimental-context-list";
	}
	

	
	
}

