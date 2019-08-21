package org.nextprot.api.core.service;

import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigesterBuilder;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigestion;
import org.nextprot.api.core.domain.DigestedPeptide;

import java.util.List;
import java.util.Set;

public interface DigestionService {

	Set<DigestedPeptide> digestProteins(String entryOrIsoformAccession, ProteinDigesterBuilder builder) throws ProteinDigestion.MissingIsoformException;
	Set<String> digestAllMatureProteinsWithTrypsin();
	List<String> getProteaseNames();
}
