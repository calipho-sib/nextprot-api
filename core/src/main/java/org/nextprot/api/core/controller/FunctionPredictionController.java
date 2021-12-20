package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.core.domain.AggregateFunctionPrediction;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Controller to handle function predictions
 */
@Lazy
@Controller
@Api(name = "Function Predictions", description = "Method to access function predictions", group = "Function Predictions")
public class FunctionPredictionController {

    @Autowired
    FunctionPredictionService functionPredictionService;

    @AllowedAnonymous
    @ApiMethod(verb = ApiVerb.GET, description = "Gets all function predictions", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/function-predictions/{entry}", method = { RequestMethod.GET })
    @ResponseBody
    public AggregateFunctionPrediction getFunctionPrediction(
            @ApiPathParam(name = "entry", description = "Entry accession",  allowedvalues = { "NX_P52701"})
            @PathVariable("entry")  String entryAccession) {
        return functionPredictionService.getFunctionPredictions(entryAccession);
    }
    
    // pam: we keep this API method undocumented in the API page
    //@ApiMethod(verb = ApiVerb.GET, description = "Gets errors in function prediction data", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/invalid-predictions", method = { RequestMethod.GET })
    @ResponseBody
    public Map<String,List<String>> getInvalidPredictions() {
        return functionPredictionService.getInvalidPredictions();
    }

}
