package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.SlimIsoform;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;
import java.util.Set;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromIsoformAccession;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

	/**
	 * Find the isoform of the given entry matching the given name
	 * @param entryAccession the entry accession
	 * @param name the isoform name (any of its name among accession, synonyms...)
	 * @return the found isoform or null if not found
	 */
	Isoform findIsoformByName(String entryAccession, String name);

	List<Isoform> getOtherIsoforms(String isoformUniqueName);

	/**
	 * Extract and format in PEFF header all isoform required information
	 * @param isoformAccession the isoform accession to format as PEFF
	 * @return a PEFF header pojo
	 */
	IsoformPEFFHeader formatPEFFHeader(String isoformAccession);

	/**
	 * Extract and return a list of isoform sets. 
	 * Each set contains the accession of isoforms having the same sequence (= same md5 value)
	 * 
	 * @return a list of sets, each set contains at least 2 isoform accession numbers.
	 */
	List<Set<String>> getSetsOfEquivalentIsoforms();
	
	/**
	 * Extract and return a list of entry sets. 
	 * Each set contains the accession of entries having an equivalent isoform (isoform with same sequence = same md5 value)
	 * 
	 * @return a list of sets, each set contains at least 2 entry accession numbers.
	 */
	List<Set<String>> getSetsOfEntriesHavingAnEquivalentIsoform();

	List<SlimIsoform> findListOfIsoformAcMd5Sequence();

    Isoform getIsoformByNameOrCanonical(String entryNameOrIsoformName);

	default Isoform findIsoform(String isoformAccession) {

		return findIsoformByName(findEntryAccessionFromIsoformAccession(isoformAccession), isoformAccession);
	}
}
