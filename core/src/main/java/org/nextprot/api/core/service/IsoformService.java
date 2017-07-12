package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

	Isoform findIsoformByName(Entry entry, String name);
}
