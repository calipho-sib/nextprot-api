package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.Isoform;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

}
