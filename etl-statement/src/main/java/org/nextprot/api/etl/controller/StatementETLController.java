package org.nextprot.api.etl.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.StatementSourceEnum;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@Api(name = "ETL", description = "Extract Transform And Load Statements", group="ETL")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
//@ApiAuthBasic(roles={"ROLE_ADMIN"})
public class StatementETLController {

	@ApiMethod(path = "/etl/{source}/{release}", verb = ApiVerb.GET, description = "Validate isoform feature", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/etl/{source}/{release}", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String loadStatements(
			@ApiPathParam(name = "source", description = "The source to load from", allowedvalues = { "BioEditor" }) @PathVariable("source") String source,
			@ApiPathParam(name = "release", description = "The release date ", allowedvalues = { "2018-10-04" }) @PathVariable("release") String release,
			HttpServletRequest request) {

		boolean load = true;

		if ("true".equalsIgnoreCase(request.getParameter("skipLoad"))){
			load = false;
		}

		try {
			StatementSourceEnum statementSource = StatementSourceEnum.valueOfKey(source);

			return statementSource.extractTransformLoadStatements(release, load);
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}
	}
}
