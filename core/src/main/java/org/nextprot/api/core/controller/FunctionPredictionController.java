package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.core.domain.FunctionPrediction;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    @RequestMapping(value = "/function-predictions", method = { RequestMethod.GET })
    @ResponseBody
    public List<FunctionPrediction> getFunctionPrediction() {
        return functionPredictionService.getFunctionPredictions();
    }
}
