package org.nextprot.api.core.service;

import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigesterBuilder;

import java.util.List;
import java.util.Set;

public interface DigestionService {

	Set<String> digestProteins(String entryOrIsoformAccession, ProteinDigesterBuilder builder);
	Set<String> digestAllMatureProteinsWithTrypsin();
	List<String> getProteaseNames();
}
