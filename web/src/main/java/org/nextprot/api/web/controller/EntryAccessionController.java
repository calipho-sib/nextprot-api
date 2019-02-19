package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.web.service.impl.writer.JSONObjectsWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@Api(name = "Entry Accessions", description = "Retrieves nextProt entry accession numbers")
public class EntryAccessionController {

	@Autowired private MasterIdentifierService masterIdentifierService;

    @ApiMethod(path = "/entry-accessions", verb = ApiVerb.GET, description = "Retrieves all neXtProt entry accession numbers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @RequestMapping(value = "/entry-accessions", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE} )
    public void masterIdentifiers(HttpServletRequest request, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try {
            writeEntries(masterIdentifierService.findUniqueNames(), mediaType, response);
        } catch (IOException e) {
            throw new NextProtException("cannot get all entries in "+mediaType.getExtension()+" format", e);
        }
    }

    //@ApiMethod(path = "/entry-accessions/random/{n}", verb = ApiVerb.GET, description = "Retrieves n random neXtProt entry accession numbers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @RequestMapping(value = "/entry-accessions/random/{n}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE} )
    public void getRandomEntryAccessions(HttpServletRequest request,
                                         //@ApiPathParam(name = "n", description = "the number of random entry accessions",  allowedvalues = { "1"})
                                         @PathVariable("n")  int n, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try {
            List<String> allShuffledAccessions = new ArrayList<>(masterIdentifierService.findUniqueNames());

            if (n <= 0 || n > allShuffledAccessions.size()) {
                throw new NextProtException("invalid n value (n="+ n +"): cannot get random entries from invalid n value (n should positive and less equal than "+ allShuffledAccessions.size()+")");
            }

            Collections.shuffle(allShuffledAccessions);

            writeEntries(allShuffledAccessions.subList(0, n), mediaType, response);
        } catch (IOException e) {
            throw new NextProtException("cannot get random entries in "+mediaType.getExtension()+" format", e);
        }
    }

    @ApiMethod(path = "/entry-accessions/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Retrieve all neXtProt entry accession numbers of the given chromosome", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @RequestMapping(value = "/entry-accessions/chromosome/{chromosome}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE} )
    public void masterIdentifiersByChromosome(HttpServletRequest request,
                                               @ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
                                               @PathVariable("chromosome")  String chromosome, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try {
            writeEntries(masterIdentifierService.findUniqueNamesOfChromosome(chromosome), mediaType, response);
        } catch (IOException e) {
            throw new NextProtException("cannot export entries by ProteinExistence in "+mediaType.getExtension()+" format", e);
        }
    }

    @ApiMethod(path = "/entry-accessions/gene/{geneName}", verb = ApiVerb.GET, description = "Retrieves the entry accession number(s) corresponding to the given gene name", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @RequestMapping(value = "/entry-accessions/gene/{geneName}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE} )
    public void masterIdentifierByGeneName(HttpServletRequest request,
                                           @ApiPathParam(name = "geneName", description = "The gene name",  allowedvalues = { "INSR"})
                                           @PathVariable("geneName")  String geneName,
                                           @ApiQueryParam(name = "synonym", description = "Search gene name synonyms if set to true.",  allowedvalues = { "true" })
                                           @RequestParam(value = "synonym", required = false) boolean withSynonyms, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try {
            writeEntries(masterIdentifierService.findEntryAccessionByGeneName(geneName, withSynonyms), mediaType, response);
        } catch (IOException e) {
            throw new NextProtException("cannot export entries by ProteinExistence in "+mediaType.getExtension()+" format", e);
        }
    }

    @ApiMethod(path = "/entry-accessions/protein-existence/{proteinExistence}", verb = ApiVerb.GET, description = "Retrieves the entry accession number(s) corresponding to the given protein existence type", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	@RequestMapping(value = "/entry-accessions/protein-existence/{proteinExistence}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE} )
	public void masterIdentifierByProteinExistence(HttpServletRequest request, @ApiPathParam(name = "proteinExistence", description = "The protein existence value type (PROTEIN_LEVEL, TRANSCRIPT_LEVEL, HOMOLOGY, PREDICTED, UNCERTAIN)",
            allowedvalues = { "PROTEIN_LEVEL"}) @PathVariable("proteinExistence") String proteinExistence, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

		try {
            writeEntries(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.valueOfKey(proteinExistence)), mediaType, response);
		} catch (IOException e) {
			throw new NextProtException("cannot export entries by ProteinExistence in "+mediaType.getExtension()+" format", e);
		}
	}

	private void writeEntries(Collection<String> entries, NextprotMediaType mediaType, HttpServletResponse response) throws IOException {

        if (mediaType == NextprotMediaType.JSON) {

            JSONObjectsWriter<String> writer = new JSONObjectsWriter<>(response.getOutputStream());
            writer.write(entries);
        }
        else if (mediaType == NextprotMediaType.TXT) {
            PrintWriter writer = new PrintWriter(response.getOutputStream());

            entries.forEach(entryAccession -> {
                writer.write(entryAccession);
                writer.write("\n");
            });

            writer.close();
        }
    }
}
