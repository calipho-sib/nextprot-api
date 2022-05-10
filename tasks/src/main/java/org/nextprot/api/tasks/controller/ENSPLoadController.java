package org.nextprot.api.tasks.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.tasks.service.ENSPLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Api(name = "Sequence data", description = "Sequence data with ENS(*) identifiers ", group="Task")
public class ENSPLoadController {

    @Autowired
    ENSPLoadService enspLoadService;

    @ResponseBody
    @RequestMapping(value = "/tasks/sequence-data", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiMethod(path = "/tasks/sequence-data", verb = ApiVerb.GET, description = "Load the ENSP sequences for each nextprot Isoform")
    public List<Map<String, Object>> loadENSPSequences() {
        return enspLoadService.loadENSPSequences();
    }
}
