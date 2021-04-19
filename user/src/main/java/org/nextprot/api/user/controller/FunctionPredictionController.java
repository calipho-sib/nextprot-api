package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to handle function predictions
 */
@Lazy
@Controller
@Api(name = "Function Predictions", description = "Method to access queries without authentication (SPARQL)", group = "Function Predictions")
public class FunctionPredictionController {

    @ApiMethod(verb = ApiVerb.GET, description = "Gets all function predictions", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/functionpredictions", method = { RequestMethod.GET })
    @ResponseBody
    public String getLists() {
        return "Predictions";
    }
}
