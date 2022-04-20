package org.nextprot.api.rdf.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RDFDereferencingController {

    @Value("${sparql.url}")
    private String sparqlEndpoint;

    @RequestMapping(value = "/rdf/{entity}/{id}")
    @ResponseBody
    public String dereferenceRDFByContentType(
            @PathVariable("entity") String entity,
            @PathVariable("id") String id,
            HttpServletRequest request, HttpServletResponse response) {
        return entity+id;
    }

}
