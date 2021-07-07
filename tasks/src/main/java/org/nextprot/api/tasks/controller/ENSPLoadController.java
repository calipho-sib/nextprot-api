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

@Controller
@Api(name = "ENSP loading tasks", description = "ENSP sequence loading task", group="Task")
public class ENSPLoadController {

    @Autowired
    ENSPLoadService enspLoadService;

    @ResponseBody
    @RequestMapping(value = "/tasks/ensp-load", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ApiMethod(path = "/tasks/ensp-load", verb = ApiVerb.GET, description = "Load the ENSP sequences for each nextprot Isoform")
    public String loadENSPSequences() {

        enspLoadService.loadENSPSequences();
        return null;
    }
}
