package org.nextprot.api.rdf.controller;

import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.api.rdf.service.RDFDereferencingService;
import org.nextprot.api.rdf.service.impl.HttpSparqlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class RDFDereferencingController {

    private final RDFDereferencingService rdfDereferencingService;

    private final String ACCEPT_HEADER = "Accept";

    public RDFDereferencingController(RDFDereferencingService rdfDereferencingService) {
        this.rdfDereferencingService = rdfDereferencingService;
    }

    @RequestMapping(value = {"/rdf/{entity}/{accession}", "/rdf/{entity}/"},
            produces = {"application/rdf+xml", MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, NextprotMediaType.TURTLE_MEDIATYPE_VALUE})
    @ResponseBody
    public String dereferenceRDFByContentType(
            @PathVariable("entity") String entity,
            @PathVariable("accession") Optional<String> accession,
            HttpServletRequest request, HttpServletResponse response) {

        // Response content type determined by the request content type header
        String requestedContentType = "json";
        Enumeration<String> acceptHeaders = request.getHeaders(ACCEPT_HEADER);
        while(acceptHeaders.hasMoreElements()) {
            String header = acceptHeaders.nextElement();
            if(header.contains("rdf+xml") || header.contains("xml")) {
                requestedContentType = "xml";
            } else if(header.contains("text/turtle")) {
                requestedContentType = "ttl";
            }
        }
        String sparqlResponse = rdfDereferencingService.generateRDFContent(entity, accession, requestedContentType);
        return sparqlResponse;
    }

}
