package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.Isoform;
import org.nextprot.api.service.annotation.ValidEntry;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

}
