package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationView;
import org.nextprot.api.core.service.EntryPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@Api(name = "Entry Publications", description = "Method to retrieve a publications linked to a neXtProt entry")
public class EntryPublicationController {

	@Autowired private EntryPublicationService entryPublicationService;

	@ApiMethod(path = "/entry-publications/report/{entry}", verb = ApiVerb.GET, description = "Exports publications associated with a neXtProt entry",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/report/{entry}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublications exportEntryPublicationReport(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryPublicationService.findEntryPublications(entryName);
	}

	@ApiMethod(path = "/entry-publications/{entry}", verb = ApiVerb.GET, description = "Exports publications associated with a neXtProt entry",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}", method = { RequestMethod.GET })
	@ResponseBody
	public Collection<EntryPublication> getEntryPublicationList(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiQueryParam(name = "publication-view", defaultvalue = "CURATED", description = "optional publication view (value among CURATED, ADDITIONAL, PATENT, SUBMISSION, WEB_RESOURCE)", allowedvalues = { "CURATED" })
			@RequestParam(value = "publication-view", required = false) String publicationView) {

		if (publicationView == null) {
			return entryPublicationService.findEntryPublications(entryName).getEntryPublicationList();
		}
		else if (PublicationView.hasName(publicationView.toUpperCase())) {

			return entryPublicationService.findEntryPublications(entryName)
					.getEntryPublicationList(PublicationView.valueOf(publicationView.toUpperCase()));
		}

		throw new NextProtException(publicationView + ": Invalid value for 'publication-view' parameter");
	}

	@ApiMethod(path = "/entry-publications/{entry}/{id}", verb = ApiVerb.GET, description = "Export identified publication associated with a neXtProt entry",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}/{pubid}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublication getEntryPublication(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
			@PathVariable("pubid") long publicationId) {

		return entryPublicationService.findEntryPublications(entryName).getEntryPublication(publicationId);
	}

    @ApiMethod(path = "/entry-publications/{entry}/count", verb = ApiVerb.GET, description = "Count entry publications associated with a neXtProt entry by category",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/entry-publications/{entry}/count", method = { RequestMethod.GET })
    @ResponseBody
    public Map<PublicationView, Integer> countEntryPublication(
            @ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
            @PathVariable("entry") String entryAccession) {

	    Map<PublicationView, Integer> count = new HashMap<>();

	    for (PublicationView view : PublicationView.values()) {

            count.put(view, entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationList(view).size());
        }

        return count;
    }
}
