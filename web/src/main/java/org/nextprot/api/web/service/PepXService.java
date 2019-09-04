package org.nextprot.api.web.service;

import java.util.List;

import org.nextprot.api.core.domain.Entry;

public interface PepXService {

	List<Entry> findEntriesWithPeptides(String peptide, boolean modeIsoleucine, String method);
	List<Entry> findEntriesWithPeptides(String peptides, boolean modeIsoleucine, String method, boolean ignoreVariantMatches);
	
}
