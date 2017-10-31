package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationView;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@Api(name = "Entry Publications", description = "Method to retrieve a publications linked to a neXtProt entry")
public class EntryPublicationController {

	@Autowired
    private EntryPublicationService entryPublicationService;
    @Autowired
    private PublicationService publicationService;

	@ApiMethod(path = "/entry-publications/{entry}/view/{view}", verb = ApiVerb.GET, description = "Exports publications associated with a neXtProt entry and a publication view",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}/view/{view}", method = { RequestMethod.GET })
	@ResponseBody
	public List<EntryPublicationView> getEntryPublicationList(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "view", allowedvalues = { "CURATED" })
			@PathVariable(value = "view") String publicationView) {

        String pubViewName = publicationView.toUpperCase();

		if (PublicationView.hasName(pubViewName)) {

            return buildView(entryPublicationService.findEntryPublications(entryName), PublicationView.valueOfName(pubViewName));
		}

		throw new NextProtException(publicationView + ": Unknown publication view");
	}

	@ApiMethod(path = "/entry-publications/{entry}/pub-id/{pubid}", verb = ApiVerb.GET, description = "Exports identified publication associated with a neXtProt entry",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}/pub-id/{pubid}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublication getEntryPublication(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
			@PathVariable("pubid") long publicationId) {

		return entryPublicationService.findEntryPublications(entryName).getEntryPublication(publicationId);
	}

    @ApiMethod(path = "/entry-publications/{entry}/count", verb = ApiVerb.GET, description = "Count entry publications associated with a neXtProt entry by publication view",
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

    private List<EntryPublicationView> buildView(EntryPublications entryPublications, PublicationView publicationView) {

        List<EntryPublicationView> list = new ArrayList<>();

        Map<Long, EntryPublication> entryPublicationMap = entryPublications
                .getEntryPublicationList(publicationView).stream()
                .collect(Collectors.toMap(
                        EntryPublication::getPubId,
                        Function.identity(),
                        (entryPublication, entryPublication2) -> entryPublication
                ));

        List<Publication> publications = publicationService.findPublicationsByEntryName(entryPublications.getEntryAccession());

        for (Publication publication : publications) {

            if (entryPublicationMap.containsKey(publication.getPublicationId())) {
                EntryPublicationView view = new EntryPublicationView();

                EntryPublication entryPublication = entryPublicationMap.get(publication.getPublicationId());

                view.setCitedInViews(entryPublication.getCitedInViews());
                view.setDirectLinks(entryPublication.getDirectLinks());
                view.setPublication(publication);

                list.add(view);
            }
        }

        return list;
    }

    public static class EntryPublicationView implements Serializable {

        private static final long serialVersionUID = 1L;

        private Publication publication;
        private Map<String,String> citedInViews;
        private List<PublicationDirectLink> directLinks;

        public Publication getPublication() {
            return publication;
        }

        public void setPublication(Publication publication) {
            this.publication = publication;
        }

        public Map<String, String> getCitedInViews() {
            return citedInViews;
        }

        public void setCitedInViews(Map<String, String> citedInViews) {
            this.citedInViews = citedInViews;
        }

        public List<PublicationDirectLink> getDirectLinks() {
            return directLinks;
        }

        public void setDirectLinks(List<PublicationDirectLink> directLinks) {
            this.directLinks = directLinks;
        }
    }
}
