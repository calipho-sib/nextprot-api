package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.annotation.ValidEntry;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

}
