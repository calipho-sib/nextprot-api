package org.nextprot.api.rdf.controller;

import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.rdf.service.HttpSparqlService;
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

@Controller
public class RDFDereferencingController {

    @Value("${sparql.url}")
    private String sparqlEndpoint;

    private final String ACCEPT_HEADER = "Accept";

    @Autowired
    private HttpSparqlService sparqlService;

    @RequestMapping(value = "/rdf/{entity}/{id}", produces = {MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, NextprotMediaType.TURTLE_MEDIATYPE_VALUE})
    @ResponseBody
    public String dereferenceRDFByContentType(
            @PathVariable("entity") String entity,
            @PathVariable("id") String id,
            HttpServletRequest request, HttpServletResponse response) {

        // Response content type determined by the request content type header
        String requestedContentType = "json";
        Enumeration<String> acceptHeaders = request.getHeaders(ACCEPT_HEADER);
        while(acceptHeaders.hasMoreElements()) {
            String header = acceptHeaders.nextElement();
            if(header.contains("rdf+xml")) {
                requestedContentType = "rdf";
            } else if(header.contains("xml")) {
                requestedContentType = "xml";
            } else if(header.contains("text/turtle")) {
                requestedContentType = "ttl";
            }
        }
        String sparqlResponse = sparqlService.executeSparqlQuery(HttpSparqlServiceImpl.SPARQL_DEFAULT_URL,"DESCRIBE :Entry", requestedContentType);
        System.out.println(sparqlResponse);
        //response.setHeader("Content-Type", requestedContentType);
        return sparqlResponse;
    }

}
