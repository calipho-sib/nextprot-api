package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

public interface ExonMappingService {

	/**
	 * @return a list of exons mapping a gene/ENST/isoform sorted by gene location
	 */
	List<ExonMapping> findExonMappings(@ValidEntry String entryAccession, String geneName);
}