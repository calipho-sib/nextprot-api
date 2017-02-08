package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.MainNames;

import java.util.Map;

/**
 * Extracts main names of proteins and isoforms based on isoform_identifier_view
 * 
 * @author pam
 */
public interface MainNamesService {

	Map<String,MainNames> findIsoformOrEntryMainName();

	/**
	 * Extract main names of a given protein or isoform
	 * @param accession isoform or entry accession
	 * @return a MainNames object
	 */
	default MainNames findIsoformOrEntryMainName(String accession) {
		return findIsoformOrEntryMainName().get(accession);
	}
}
