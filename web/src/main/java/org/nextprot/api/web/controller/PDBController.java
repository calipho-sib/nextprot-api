package org.nextprot.api.web.controller;

import org.nextprot.api.web.service.PDBProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PDBController {

	@Autowired
	private PDBProxyService pdbProxyService;

	@RequestMapping(value = "/pdb/{entry}",  method = { RequestMethod.GET }, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String mirrorPDB(@PathVariable("entry") String entryName) {

		return this.pdbProxyService.findPdbEntry(entryName);
	}
	
	@RequestMapping(value = "/pdbx/{entry}",  method = { RequestMethod.GET }, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String mirrorPDBX(@PathVariable("entry") String entryName) {

		return this.pdbProxyService.findPdbxEntry(entryName);
	}
}
